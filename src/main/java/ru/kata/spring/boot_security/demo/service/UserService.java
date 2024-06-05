package ru.kata.spring.boot_security.demo.service;


import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import java.util.List;


public interface UserService {
    List<User> getAllUsers();
    User findUserById(long id);
    User findUserByName(String name);
    void createUser(UserDTO userDTO);
    void updateUser(UserDTO userDTO);
    void deleteUser(long id);
}
