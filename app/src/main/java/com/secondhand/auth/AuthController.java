package com.secondhand.auth;

import com.secondhand.user.User;
import com.secondhand.user.UserService;

import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
        @RequestBody Map<String, String> request
    ) {
        Map<String, String> response = new HashMap<>();

        String jwt = authenticateAndGetToken(
            request.get("username"),
            request.get("password")
        );

        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
        @RequestBody Map<String, String> request
    ) {
        Map<String, String> response = new HashMap<>();

        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");

        if (username == null || username.isEmpty()) {
            response.put("message", "Username cannot be empty!");
            return ResponseEntity.badRequest().body(response);
        }

        if (email == null || email.isEmpty()) {
            response.put("message", "Email cannot be empty!");
            return ResponseEntity.badRequest().body(response);
        }

        if (password == null || password.isEmpty()) {
            response.put("message", "Password cannot be empty!");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.getUserByUsername(username).isPresent()) {
            response.put("message", "Username already exists!");
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.getUserByEmail(email).isPresent()) {
            response.put("message", "Email already exists!");
            return ResponseEntity.badRequest().body(response);
        }

        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        //user.setPassword(passwordEncoder.encode(password));

        userService.saveUser(user);

        String jwt = authenticateAndGetToken(username, password);

        response.put("message", "User registered successfully!");
        response.put("token", jwt);

        return ResponseEntity.ok(response);
    }

    private String authenticateAndGetToken(String username, String password) {

        /*Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                username,
                password
            )
        );*/

        //return tokenProvider.generateJwtToken(authentication);
        return null;
    }
}
