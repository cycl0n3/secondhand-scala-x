package com.secondhand.user;

import com.secondhand.role.Role;
import com.secondhand.role.RoleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {
        List<User> users = userService.getAllUsers();

        if(users.isEmpty()) {
            logger.error("No users found.");
            return ResponseEntity.noContent().build();
        }

        List<UserDto> userDtos = userMapper.toUserDto(users);

        // set password to stars for security reasons

        userDtos.forEach(userDto -> userDto.setPassword("*****"));

        Map<String, Object> response = new HashMap<>();
        response.put("users", userDtos);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // save user to database if username and email doesn't exist
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveUser(@Valid @RequestBody UserDto userDto) {
        Optional<User> userByUsername = userService.getUserByUsername(userDto.getUsername());
        Optional<User> userByEmail = userService.getUserByEmail(userDto.getEmail());

        if(userByUsername.isPresent()) {
            logger.error("Username {} already exists.", userDto.getUsername());
            return ResponseEntity.badRequest().build();
        }

        if(userByEmail.isPresent()) {
            logger.error("Email {} already exists.", userDto.getEmail());
            return ResponseEntity.badRequest().build();
        }

        User user = userMapper.toUser(userDto);

        User savedUser = userService.saveUser(user);

        UserDto savedUserDto = userMapper.toUserDto(savedUser);

        // set password to stars for security reasons

        savedUserDto.setPassword("*****");

        Map<String, Object> response = new HashMap<>();
        response.put("user", savedUserDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable @Min(0) Long userId) {
        Optional<User> user = userService.getUserById(userId);

        if(user.isEmpty()) {
            logger.error("User with id {} not found.", userId);
            return ResponseEntity.noContent().build();
        }

        UserDto userDto = userMapper.toUserDto(user.get());

        // set password to stars for security reasons

        userDto.setPassword("*****");

        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);

        if(user.isEmpty()) {
            logger.error("User with username {} not found.", username);
            return ResponseEntity.noContent().build();
        }

        // set password to stars for security reasons

        UserDto userDto = userMapper.toUserDto(user.get());

        userDto.setPassword("*****");

        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // add role to user and check if that role exists for that user
    @PostMapping("/addRole/{userId}/{roleId}")
    public ResponseEntity<Map<String, Object>> addRoleToUser(@PathVariable @Min(0) Long userId, @PathVariable @Min(0) Long roleId) {
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

        UserDto updatedUserDto = userMapper.toUserDto(updatedUser);

        // set password to stars for security reasons

        updatedUserDto.setPassword("*****");

        Map<String, Object> response = new HashMap<>();
        response.put("user", updatedUserDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
