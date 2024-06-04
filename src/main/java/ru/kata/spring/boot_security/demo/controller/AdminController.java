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
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
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
        userService.updateUser(id, user, stringRoles);
        return "redirect:/admin/users";
    }

    @PostMapping("/users")
    public String addUser(@ModelAttribute("user") @Valid User user,
                          @RequestParam(value = "stringRoles", required = false) List<String> stringRoles) {
        userService.createUser(user, stringRoles);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
