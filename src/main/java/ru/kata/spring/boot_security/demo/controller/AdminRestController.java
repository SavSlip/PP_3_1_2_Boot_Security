package ru.kata.spring.boot_security.demo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;


@RestController
@RequestMapping("/admin")
public class AdminRestController {

    private final UserService userService;

    private final RoleService roleService;

    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("/edit/{id}")
    public void updateUser(@RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
    }

    @PostMapping("/users")
    public void addUser(@RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);

    }

    @PostMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/api/roles")
    public List<Role> getAllRoles() {
        return roleService.findAll();
    }

    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/api/users/{userId}")
    public User getUserById(@PathVariable Long userId) {
        return userService.findUserById(userId);
    }
}
