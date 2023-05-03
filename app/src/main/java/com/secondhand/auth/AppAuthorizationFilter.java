package com.secondhand.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import scala.Tuple2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class AppAuthorizationFilter extends OncePerRequestFilter {

    private final AuthTokenProvider authTokenProvider;

    private static final String[] ALLOWED_PATHS = {
        "/api/v1/auth/",
    };

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Securing {}: ", request.getServletPath());

        if(Arrays.stream(ALLOWED_PATHS).anyMatch(request.getServletPath()::startsWith)) {
            log.info("Allowed: {}", request.getServletPath());

            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring(7);

                    Tuple2<String, List<GrantedAuthority>> tuple = authTokenProvider.verifyAndGetAuthorities(token);

                    String username = tuple._1();
                    List<GrantedAuthority> authorities = tuple._2();

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error authorization: {}", e.getMessage());

                    Map<String, String> error = Map.of("error_message", e.getMessage());

                    response.setHeader("error", e.getMessage());
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");

                    response.getWriter().write(new ObjectMapper().writeValueAsString(error));
                }
            } else {
                log.info("Authorization header is missing");

                filterChain.doFilter(request, response);
            }
        }
    }
}
