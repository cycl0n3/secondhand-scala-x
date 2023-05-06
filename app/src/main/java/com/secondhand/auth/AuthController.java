package com.secondhand.auth;

import com.secondhand.user.User;
import com.secondhand.user.UserService;

import jakarta.servlet.http.HttpServletRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final AuthTokenProvider authTokenProvider;

    private PasswordEncoder passwordEncoder;

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(
        HttpServletRequest request
    ) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring(7);
                Map<String, Object> tokens = authTokenProvider.verifyAndGenerateTokens(token);

                return ResponseEntity.ok(tokens);
            } catch (Exception exception) {
                log.error("Error: {}", exception.getMessage());

                Map<String, String> error = Map.of("error_message", exception.getMessage());

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
        } else {
            Map<String, String> error = Map.of("error_message", "Refresh token is missing");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // user registration using rest api
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
        @RequestParam("username") String username,
        @RequestParam("email") String email,
        @RequestParam("password") String password
    ) {

        Optional<User> userByUsername = userService.getUserByUsername(username);
        Optional<User> userByEmail = userService.getUserByEmail(email);

        if (userByUsername.isPresent() || userByEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        User user = new User();

        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userService.saveUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
