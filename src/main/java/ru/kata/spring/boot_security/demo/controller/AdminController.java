package ru.kata.spring.boot_security.demo.controller;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
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

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminController(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users")
    public String getAllUsers(@ModelAttribute("user") User user, Model model, Principal principal) {
        User currentUser = userService.findUserByName(principal.getName());
        model.addAttribute("usersList", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);
        List<Role> roleList = roleRepository.findAll();
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
            List<Role> roles = stringRoles.stream().map(roleString -> roleRepository.findByName(roleString).get()).collect(Collectors.toList());
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
            user.setRoles(Collections.singletonList(roleRepository.findById(1L).get()));
        } else {
            List<Role> roles = stringRoles.stream().map(roleString -> roleRepository.findByName(roleString).get()).collect(Collectors.toList());
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
