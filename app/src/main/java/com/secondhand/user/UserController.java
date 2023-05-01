package com.secondhand.user;

import com.secondhand.role.Role;
import com.secondhand.role.RoleMapper;
import com.secondhand.role.RoleService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {
        List<User> users = userService.getAllUsers();

        if(users.isEmpty()) {
            logger.error("No users found.");
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();

        List<UserDto> userDtos = userMapper.toUserDto(users);

        // set password to stars for security reasons

        userDtos.forEach(userDto -> userDto.setPassword("*****"));

        response.put("users", userDtos);

        return ResponseEntity.ok(response);
    }

    // save user to database
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveUser(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);

        User savedUser = userService.saveUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMapper.toUserDto(savedUser));

        URI location = URI.create(String.format("/api/v1/user/id/%s", savedUser.getId()));

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);

        if(user.isEmpty()) {
            logger.error("User with id {} not found.", userId);
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();

        // set password to stars for security reasons

        UserDto userDto = userMapper.toUserDto(user.get());
        userDto.setPassword("*****");

        response.put("user", userDto);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);

        if(user.isEmpty()) {
            logger.error("User with username {} not found.", username);
            return ResponseEntity.noContent().build();
        }

        Map<String, Object> response = new HashMap<>();

        // set password to stars for security reasons

        UserDto userDto = userMapper.toUserDto(user.get());

        userDto.setPassword("*****");
        response.put("user", userDto);

        return ResponseEntity.ok(response);
    }

    // add role to user and check if that role exists for that user
    @PostMapping("/addRole/{userId}/{roleId}")
    public ResponseEntity<Map<String, Object>> addRoleToUser(@PathVariable Long userId, @PathVariable Long roleId) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Role> role = roleService.getRoleById(roleId);

        if(user.isEmpty()) {
            logger.error("User with id {} not found.", userId);
            return ResponseEntity.noContent().build();
        }

        if(role.isEmpty()) {
            logger.error("Role with id {} not found.", roleId);
            return ResponseEntity.noContent().build();
        }

        if(user.get().getRoles().contains(role.get())) {
            logger.error("User with id {} already has role with id {}.", userId, roleId);
            return ResponseEntity.unprocessableEntity().build();
        }

        User updatedUser = userService.addRoleToUser(user.get().getUsername(), role.get().getName());

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMapper.toUserDto(updatedUser));

        URI location = URI.create(String.format("/api/v1/user/id/%s", updatedUser.getId()));

        return ResponseEntity.created(location).body(response);
    }
}
