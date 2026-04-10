package com.keyin.ticketnestbackend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JwtUtil is a utility class that provides methods for generating and validating JWT tokens.
 * It uses the jjwt library to create and parse JWT tokens.
 * The secret key for signing the tokens is injected from the application properties
 * using the @Value annotation. The class provides methods to generate
 */
@Component
public class JwtUtil {

    /**
     * The secret key used for signing the JWT tokens.
     */
    private final Key key;

    /**
     * The validity period of the JWT tokens in milliseconds.
     */
    private final long validityMs = 24 * 60 * 60 * 1000L; // 24h

    /**
     * Constructor for JwtUtil that takes a secret key as a parameter.
     * @param secret the secret key used for signing the JWT tokens.
     */
    public JwtUtil(@Value("${JWT_SECRET}") String secret) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT_SECRET must be set and at least 32 chars");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a JWT token for the given username.
     * @param username the username for which the token is generated.
     * @return a JWT token as a String that contains the username as the subject, the issued date, and the expiration date.
     */
    public String generateToken(String username) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + validityMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the given JWT token. It checks if the token is well-formed, not expired, and signed with the correct key.
     * @param token the JWT token to validate.
     * @return true if the token is valid, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Extracts the username from the given JWT token.
     * It parses the token and retrieves the subject, which is the username.
     * @param token the JWT token from which to extract the username.
     * @return the username extracted from the token, or null if the token is invalid.
     */
    public String getUsernameFromToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return claims.getBody().getSubject();
    }

    /**
     * Validates the given JWT token and returns the username if the token is valid.
     * @param token the JWT token to validate and extract the username from.
     * @return the username if the token is valid, null otherwise.
     */
    public String validateAndGetUsername(String token) {
        return getUsernameFromToken(token);
    }
}
