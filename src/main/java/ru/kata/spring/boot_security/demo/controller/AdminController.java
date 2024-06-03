package ru.kata.spring.boot_security.demo.controller;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String getAllUsers(@ModelAttribute("user") User user, Model model, Principal principal) {
        User currentUser = userService.findUserByName(principal.getName());
        model.addAttribute("usersList", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);
        List<Role> roleList = roleService.findAll();
        model.addAttribute("allRoles", roleList);
        return "showAllUsers";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@RequestParam(value = "roles", required = false) List<String> stringRoles,
                             @ModelAttribute("user") @Valid User user,
                             @PathVariable("id") long id) {
        if (stringRoles == null) {
            user.setRoles(userService.findUserById(user.getId()).getRoles());
        } else {
            List<Role> roles = stringRoles.stream().map(roleService::findByName).collect(Collectors.toList());
            user.setRoles(roles);
        }
        if (user.getPassword().isEmpty()) {
            user.setPassword(userService.findUserById(user.getId()).getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }


    @PostMapping("/users")
    public String addUser(@ModelAttribute("user") @Valid User user,
                          @RequestParam(value = "roles", required = false) List<String> stringRoles) {
        if (stringRoles == null) {
            user.setRoles(Collections.singletonList(roleService.findById(1L)));
        } else {
            List<Role> roles = stringRoles.stream().map(roleService::findByName).collect(Collectors.toList());
            user.setRoles(roles);
        }
        if (user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("user"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
