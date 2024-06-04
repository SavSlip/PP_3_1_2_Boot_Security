package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findUserByName(String name) {
        return userRepository.findByName(name).get();
    }

    @Override
    public void createUser(User user, List<String> stringRoles) {
        if (stringRoles == null) {
            user.setRoles(Collections.singletonList(roleRepository.findById(1L).get()));
        } else {
            List<Role> roles = stringRoles.stream().map(roleRepository::findByName).map(role -> role.get()).collect(Collectors.toList());
            user.setRoles(roles);
        }
        if (user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode("user"));
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void updateUser(long id, User user, List<String> stringRoles) {
        user.setId(id);
        if (stringRoles == null) {
            user.setRoles(userRepository.findById(user.getId()).get().getRoles());
        } else {
            List<Role> roles = stringRoles.stream().map(roleRepository::findByName)
                    .map(role -> role.get()).collect(Collectors.toList());
            user.setRoles(roles);
        }
        if (user.getPassword().isEmpty()) {
            user.setPassword(userRepository.findById(user.getId()).get().getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }
}
