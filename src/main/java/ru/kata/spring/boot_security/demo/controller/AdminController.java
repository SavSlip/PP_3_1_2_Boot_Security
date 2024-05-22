package ru.kata.spring.boot_security.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
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
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public String getAllUsers(@ModelAttribute("user") User user, Model model, Principal principal) {
        User currentUser = userRepository.findByName(principal.getName()).get();
        model.addAttribute("usersList", userService.getAllUsers());
        model.addAttribute("currentUser", currentUser);
        List<Role> roleList = roleRepository.findAll();
        model.addAttribute("allRoles", roleList);

        System.out.println();
        return "showAllUsers";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@RequestParam(value = "roles", required = false) List<String> stringRoles, @ModelAttribute("user") @Valid User user, BindingResult bindingResult,
                             @PathVariable("id") long id) {
        if (stringRoles == null) {
            user.setRoles(userRepository.findById(user.getId()).get().getRoles());
        } else {
            List<Role> roles = stringRoles.stream().map(roleString -> roleRepository.findByName(roleString).get()).collect(Collectors.toList());
            user.setRoles(roles);
        }
        if (user.getPassword().isEmpty()) {
            user.setPassword(userRepository.findById(user.getId()).get().getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }


        userService.updateUser(id, user);
        return "redirect:/admin/users";
    }


    @PostMapping("/users")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult,
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
