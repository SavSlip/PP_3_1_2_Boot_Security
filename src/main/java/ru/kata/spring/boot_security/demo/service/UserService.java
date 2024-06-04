package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;


public interface UserService {
    List<User> getAllUsers();
    User findUserById(long id);
    User findUserByName(String name);
    void createUser(User user, List<String> stringRoles);
    void updateUser(long id, User user, List<String> stringRoles);
    void deleteUser(long id);
}
