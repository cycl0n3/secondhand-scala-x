package com.secondhand.auth;

import com.secondhand.role.Role;
import com.secondhand.user.UserService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AuthTokenProvider {

    public static final String SECRET = "@X/(}@2:w]=x4w$@.t[&T223q&X*E+c";
    public static final String ISSUER = "http://localhost:8080";

    private final UserService userService;

    public Map<String, String> generateTokens(User user) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());

        String accessToken = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 30 * 60 * 1000))
            .withIssuer(ISSUER)
            .withClaim("roles", user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
            .sign(algorithm);

        String refreshToken = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new java.util.Date(System.currentTimeMillis() + 60 * 60 * 1000))
            .withIssuer(ISSUER)
            .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();

        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);

        return tokens;
    }

    public Map<String, String> verifyAndGenerateTokens(String refreshToken) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(refreshToken);

        String username = decodedJWT.getSubject();

        com.secondhand.user.User user;

        Optional<com.secondhand.user.User> userByUsername = userService.getUserByUsername(username);
        Optional<com.secondhand.user.User> userByEmail = userService.getUserByEmail(username);

        if(userByUsername.isPresent()) {
            user = userByUsername.get();
        } else if(userByEmail.isPresent()) {
            user = userByEmail.get();
        } else {
            throw new RuntimeException("User not found");
        }

        String[] roles = user.getRoles().stream().map(Role::getName).toArray(String[]::new);

        return generateTokens(new User(user.getUsername(), "", Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList())));
    }

    public Tuple2<String, List<GrantedAuthority>> verifyAndGetAuthorities(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
        JWTVerifier verifier = JWT.require(algorithm).build();
        DecodedJWT decodedJWT = verifier.verify(token);

        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);

        com.secondhand.user.User user;

        Optional<com.secondhand.user.User> userByUsername = userService.getUserByUsername(username);
        Optional<com.secondhand.user.User> userByEmail = userService.getUserByEmail(username);

        if(userByUsername.isPresent()) {
            user = userByUsername.get();
        } else if(userByEmail.isPresent()) {
            user = userByEmail.get();
        } else {
            throw new RuntimeException("User not found");
        }

        return new Tuple2<>(user.getUsername(), Arrays.stream(roles)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList()));
    }
}
