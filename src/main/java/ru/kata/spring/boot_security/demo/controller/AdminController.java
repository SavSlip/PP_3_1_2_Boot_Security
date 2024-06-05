package ru.kata.spring.boot_security.demo.controller;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    private final RoleRepository roleRepository;


    public AdminController(UserService userService, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleRepository = roleRepository;
    }

    @GetMapping("/users")
    public String getAllUsers(@ModelAttribute("user") User user, Model model, Principal principal) {
        User currentUser = userService.findUserByName(principal.getName());
        model.addAttribute("usersList", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);
        List<Role> roleList = roleRepository.findAll();
        model.addAttribute("allRoles", roleList);
        return "showUser";
    }

    @PostMapping("/edit/{id}")
    public String updateUser(@RequestBody UserDTO userDTO) {

        userService.updateUser(userDTO);
        return "redirect:/admin/users";
    }

    @PostMapping("/users")
    public String addUser(@RequestBody UserDTO userDTO) {

        userService.createUser(userDTO);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {

        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
