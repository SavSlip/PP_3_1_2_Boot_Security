package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private User user;

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
    public void createUser(UserDTO userDTO) {
        user = new User();

        mappingUser(userDTO);

        user.setPassword(userDTO.getPassword());
        user.setRoles(userDTO.getRoles().stream().map(role -> roleRepository.findByName(role).get())
                .collect(Collectors.toList()));

        userRepository.save(user);
    }

    @Override
    public void updateUser(UserDTO userDTO) {

        user = userRepository.findById(userDTO.getId()).get();

        mappingUser(userDTO);

        if (!userDTO.getRoles().isEmpty()) {
            List<Role> roles = userDTO.getRoles().stream().map(roleString -> roleRepository.findByName(roleString).get()).collect(Collectors.toList());
            user.setRoles(roles);
        }

        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }

    private void mappingUser(UserDTO userDTO){
        user.setName(userDTO.getName());
        user.setLastName(userDTO.getLastName());
        user.setAge(userDTO.getAge());
        user.setEmail(userDTO.getEmail());
    }
}
