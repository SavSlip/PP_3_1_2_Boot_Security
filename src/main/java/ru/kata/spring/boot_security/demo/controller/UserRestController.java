package ru.kata.spring.boot_security.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
public class UserRestController {

    final
    UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/admin/api/users/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/user/current_user")
    public User getCurrentUser(Principal principal) {
        return userService.findUserByName(principal.getName());
    }
}
