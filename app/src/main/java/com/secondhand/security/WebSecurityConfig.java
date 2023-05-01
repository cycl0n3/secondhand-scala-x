package com.secondhand.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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

    private final PasswordEncoder passwordEncoder;

    private final AppUserDetailsService userDetailsService;

    private final AuthenticationManager authenticationManager;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests()
            .requestMatchers("/api/v1/auth/**").permitAll()

            .requestMatchers("/api/v1/user", "/api/v1/user/**")
                .permitAll()

            .requestMatchers("/api/v1/role", "/api/v1/role/**")
                .permitAll()

            .requestMatchers("/api/v1/orders", "/api/v1/orders/**")
                .hasAnyAuthority(ROLE_MAPPING.get("ROLE_ADMIN"), ROLE_MAPPING.get("ROLE_USER"))

            .requestMatchers("/api/v1/cart", "/api/v1/cart/**")
                .hasAnyAuthority(ROLE_MAPPING.get("ROLE_ADMIN"), ROLE_MAPPING.get("ROLE_USER"))

            .anyRequest()
                .authenticated();

        //http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.addFilter(new AppAuthenticationFilter(authenticationManager));

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
