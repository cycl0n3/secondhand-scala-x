package com.secondhand.security;

import com.secondhand.misc.CustomUserDetails;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class TokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        List<String> roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        byte[] signingKey = jwtSecret.getBytes();

        return Jwts.builder()
            .setHeaderParam("typ", TOKEN_TYPE)
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setId(UUID.randomUUID().toString())
            .setIssuer(TOKEN_ISSUER)
            .setAudience(TOKEN_AUDIENCE)
            .setSubject(userPrincipal.getUsername())
            .claim("rol", roles)
            .claim("name", userPrincipal.getUsername())
            .claim("preferred_username", userPrincipal.getUsername())
            .claim("email", userPrincipal.getEmail())
            .compact();
    }

    public Optional<Jws<Claims>> validateTokenAndGetJws(String token) {
        try {
            byte[] signingKey = jwtSecret.getBytes();

            return Optional.of(Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token));
        } catch (ExpiredJwtException exception) {
            log.error("Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
        } catch (SecurityException exception) {
            log.error("Request to parse JWT with invalid signature : {} failed : {}", token, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
        }

        return Optional.empty();
    }

    public static final String TOKEN_TYPE = "JWT";
    public static final String TOKEN_ISSUER = "secondhand-api";
    public static final String TOKEN_AUDIENCE = "secondhand-app";
}
