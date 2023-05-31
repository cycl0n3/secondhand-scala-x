package com.secondhand.security;

import com.secondhand.auth.AppAuthenticationFilter;
import com.secondhand.auth.AppAuthorizationFilter;

import com.secondhand.auth.AuthTokenProvider;

import com.secondhand.user.UserMapper;
import com.secondhand.user.UserService;
import lombok.AllArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationProvider authProvider;

    private final AuthTokenProvider authTokenProvider;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final UserService userService;

    private final UserMapper userMapper;

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AppAuthenticationFilter authenticationFilter = new AppAuthenticationFilter(
            authenticationConfiguration.getAuthenticationManager(),
            authTokenProvider,
            userService,
            userMapper
        );

        AppAuthorizationFilter authorizationFilter = new AppAuthorizationFilter(
            authTokenProvider
        );

        authenticationFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http.authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**").permitAll()

                .requestMatchers("/api/v1/user/picture/**")
                    .hasAnyAuthority(ROLE_MAPPING.get("ROLE_USER"))

                .requestMatchers("/api/v1/user/**")
                    .hasAnyAuthority(ROLE_MAPPING.get("ROLE_ADMIN"))

                .requestMatchers("/api/v1/role", "/api/v1/role/**")
                    .hasAnyAuthority(ROLE_MAPPING.get("ROLE_ADMIN"))

                .anyRequest()
                    .authenticated();

        http.addFilter(authenticationFilter);

        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.cors().and().csrf().disable();

        return http.build();
    }

    private static final Map<String, String> ROLE_MAPPING = Map.of(
        "ROLE_ADMIN", "ADMIN",
        "ROLE_USER", "USER",
        "ROLE_GUEST", "GUEST",
        "ROLE_MODERATOR", "MODERATOR"
    );
}
