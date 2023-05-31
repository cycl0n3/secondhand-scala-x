package com.secondhand.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.secondhand.user.UserDto;
import com.secondhand.user.UserMapper;
import com.secondhand.user.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class AppAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    private final AuthTokenProvider authTokenProvider;

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        log.info("attemptAuthentication: username {} password {}: ", username, password);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            username,
            password
        );

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        log.info("successfulAuthentication: authentication {}: ", authentication);

        User user = (User) authentication.getPrincipal();
        Optional<com.secondhand.user.User> user1 = userService.getUserByUsername(user.getUsername());

        Map<String, Object> tokens = authTokenProvider.generateTokens(user);

        UserDto userDto = userMapper.toUserDto(user1.get());

        // set password to stars
        userDto.setPassword("*****");
        tokens.put("user", userDto);

        // set picture to base64 string
        if(userDto.getPicture() != null) {
            String base64Image = java.util.Base64.getEncoder().encodeToString(userDto.getPicture());
            tokens.put("picture", base64Image);
        } else {
            tokens.put("picture", null);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(new ObjectMapper().writeValueAsString(tokens));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
