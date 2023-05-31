package com.secondhand.user;

import com.secondhand.role.Role;
import com.secondhand.role.RoleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers() {
        List<User> users = userService.getAllUsers();

        if(users.isEmpty()) {
            log.error("No users found.");
            return ResponseEntity.noContent().build();
        }

        List<UserDto> userDtos = userMapper.toUserDto(users);

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
            log.error("Username {} already exists.", userDto.getUsername());
            return ResponseEntity.badRequest().build();
        }

        if(userByEmail.isPresent()) {
            log.error("Email {} already exists.", userDto.getEmail());
            return ResponseEntity.badRequest().build();
        }

        User user = userMapper.toUser(userDto);

        User savedUser = userService.saveUser(user);

        UserDto savedUserDto = userMapper.toUserDto(savedUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", savedUserDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id/{userId}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable @Min(0) Long userId) {
        Optional<User> user = userService.getUserById(userId);

        if(user.isEmpty()) {
            log.error("User with id {} not found.", userId);
            return ResponseEntity.noContent().build();
        }

        UserDto userDto = userMapper.toUserDto(user.get());

        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String username) {
        Optional<User> user = userService.getUserByUsername(username);

        if(user.isEmpty()) {
            log.error("User with username {} not found.", username);
            return ResponseEntity.noContent().build();
        }

        UserDto userDto = userMapper.toUserDto(user.get());

        Map<String, Object> response = new HashMap<>();
        response.put("user", userDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // add role to user and check if that role exists for that user
    @PostMapping("/addRole/{userId}/{roleId}")
    public ResponseEntity<Map<String, Object>> addRoleToUser(
        @PathVariable @Min(0) Long userId,
        @PathVariable @Min(0) Long roleId
    ) {
        Optional<User> user = userService.getUserById(userId);
        Optional<Role> role = roleService.getRoleById(roleId);

        if(user.isEmpty()) {
            log.error("User with id {} not found.", userId);
            return ResponseEntity.noContent().build();
        }

        if(role.isEmpty()) {
            log.error("Role with id {} not found.", roleId);
            return ResponseEntity.noContent().build();
        }

        if(user.get().getRoles().contains(role.get())) {
            log.error("User with id {} already has role with id {}.", userId, roleId);
            return ResponseEntity.unprocessableEntity().build();
        }

        User updatedUser = userService.addRoleToUser(user.get().getUsername(), role.get().getName());

        UserDto updatedUserDto = userMapper.toUserDto(updatedUser);

        Map<String, Object> response = new HashMap<>();
        response.put("user", updatedUserDto);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // upload picture
    @PostMapping("/picture/{userId}")
    public ResponseEntity<?> uploadPicture(
        @RequestParam("file") MultipartFile file,
        @PathVariable @Min(0) Long userId
        ) {
        try {
            Optional<User> userOp = userService.getUserById(userId);

            if(userOp.isEmpty()) {
                log.error("User with id {} not found.", userId);
                return ResponseEntity.noContent().build();
            }

            InputStream in  = file.getInputStream();
            File temp = File.createTempFile("temp---1---", ".jpg");
            Files.copy(in, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);

            byte[] data = Files.readAllBytes(temp.toPath());
            userOp.get().setPicture(data);
            userService.saveUser(userOp.get());

            UserDto userDto = userMapper.toUserDto(userOp.get());

            Map<String, Object> response = new HashMap<>();
            response.put("user", userDto);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
