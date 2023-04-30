package com.secondhand.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*http.authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, "/api/orders").hasAnyAuthority(ROLE_MAPPING.get("ROLE_ADMIN"), ROLE_MAPPING.get("ROLE_USER"))
            .requestMatchers(HttpMethod.GET, "/api/users/me").hasAnyAuthority(ROLE_MAPPING.get("ROLE_ADMIN"), ROLE_MAPPING.get("ROLE_USER"))
            .requestMatchers("/api/orders", "/api/orders/**").hasAuthority(ROLE_MAPPING.get("ROLE_ADMIN"))
            .requestMatchers("/api/users", "/api/users/**").hasAuthority(ROLE_MAPPING.get("ROLE_ADMIN"))
            .requestMatchers("/public/**", "/auth/**").permitAll()
            .requestMatchers("/", "/error", "/csrf", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated();*/

        http.authorizeHttpRequests()
                .anyRequest().authenticated();

        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.cors().and().csrf().disable();

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final Map<String, String> ROLE_MAPPING = Map.of(
        "ROLE_ADMIN", "ADMIN",
        "ROLE_USER", "USER",
        "ROLE_GUEST", "GUEST",
        "ROLE_MODERATOR", "MODERATOR"
    );
}
