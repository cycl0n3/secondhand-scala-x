package com.secondhand.user;

import com.secondhand.exception.RoleNotFoundException;
import com.secondhand.exception.UserNotFoundException;

import com.secondhand.role.Role;
import com.secondhand.role.RoleRepository;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        logger.info("Saving user {}", user);

        return userRepository.save(user);
    }

    public User addRoleToUser(String username, String roleName) {
        logger.info("Adding role {} to user {}", roleName, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + roleName));

        user.getRoles().add(role);

        return userRepository.save(user);
    }

    public User removeRoleFromUser(String username, String roleName) {
        logger.warn("Removing role {} from user {}", roleName, username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with name: " + roleName));

        user.getRoles().remove(role);

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        logger.warn("Deleting user with id {}", id);

        userRepository.deleteById(id);
    }

    public void deleteUserByUsername(String username) {
        logger.warn("Deleting user with username {}", username);

        userRepository.deleteByUsername(username);
    }

    public void deleteUserByEmail(String email) {
        logger.warn("Deleting user with email {}", email);

        userRepository.deleteByEmail(email);
    }
}
