package com.secondhand.auth;

import com.auth0.jwt.exceptions.TokenExpiredException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import groovy.lang.Tuple2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
public class AppAuthorizationFilter extends OncePerRequestFilter {

    private final AuthTokenProvider authTokenProvider;

//    private static final String[] ALLOWED_PATHS = {
//        "/api/v1/auth/",
//    };

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        log.info("Securing :{} ", request.getServletPath());

//        if(Arrays.stream(ALLOWED_PATHS).anyMatch(request.getServletPath()::startsWith)) {
//            log.info("Allowed: {}", request.getServletPath());
//            filterChain.doFilter(request, response);
//        } else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring(7);
                    Tuple2<String, List<GrantedAuthority>> tuple = authTokenProvider.verifyAndGetAuthorities(token);

                    String username = tuple.getV1();
                    List<GrantedAuthority> authorities = tuple.getV2();

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    //filterChain.doFilter(request, response);
                }
                catch (TokenExpiredException e) {
                    log.error("Error authorization: {} {}", e.getClass(), e.getMessage());
                    set_error(response, e.getMessage(), HttpServletResponse.SC_UNAUTHORIZED);
                } catch (Exception e) {
                    log.error("Error authorization: {} {}", e.getClass(), e.getMessage());
                    set_error(response, e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
//            } else {
//                log.info("Authorization header is missing");
//                send_error(response, "Authorization header is missing", HttpServletResponse.SC_UNAUTHORIZED);
//            }
            }
        filterChain.doFilter(request, response);
    }


    // write an error method to servlet response
    private void set_error(HttpServletResponse response, String message, int status) throws IOException {
        //Map<String, String> error = Map.of("error_message", message);

        response.setStatus(status);

        //response.setContentType("application/json");
        //response.setCharacterEncoding("UTF-8");
        //response.getWriter().write(new ObjectMapper().writeValueAsString(error));
    }
}
