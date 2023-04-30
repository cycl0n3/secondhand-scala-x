package com.secondhand.user;

import com.secondhand.exception.GenericException;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        Map<String, Object> response = new HashMap<>();
        response.put("users", userMapper.toUserDto(users));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(Long userId) {
        User user = userService.getUserById(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMapper.toUserDto(user));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ex")
    public ResponseEntity<String> ex() throws GenericException {
        throw new GenericException("This is a generic exception from UserController.");
    }
}
