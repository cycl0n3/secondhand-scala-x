package com.secondhand.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.secondhand.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    private final AuthTokenProvider authTokenProvider;

    @GetMapping("/refresh")
    public void refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException, ServletException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String token = authorizationHeader.substring(7);

                Map<String, String> tokens = authTokenProvider.verifyAndGenerateTokens(token);

                //Map<String, String> tokens = authTokenProvider.refreshToken(token);

                /*Algorithm algorithm = Algorithm.HMAC256(AuthTokenProvider.SECRET.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(token);

                String username = decodedJWT.getSubject();
                User user;

                Optional<User> userByUsername = userService.getUserByUsername(username);
                Optional<User> userByEmail = userService.getUserByEmail(username);

                if(userByUsername.isPresent()) {
                    user = userByUsername.get();
                } else if(userByEmail.isPresent()) {
                    user = userByEmail.get();
                } else {
                    throw new RuntimeException("User not found");
                }

                String accessToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 30 * 60 * 1000))
                    .withIssuer(AuthTokenProvider.ISSUER)
                    .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                    .sign(algorithm);

                String refreshToken = JWT.create()
                    .withSubject(user.getUsername())
                    .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 60 * 60 * 1000))
                    .withIssuer(AuthTokenProvider.ISSUER)
                    .sign(algorithm);

                Map<String, String> tokens = Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken
                );*/

                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);
            } catch (Exception exception) {
                log.error("Error: {}", exception.getMessage());

                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());

                Map<String, String> error = Map.of("error_message", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);

                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
