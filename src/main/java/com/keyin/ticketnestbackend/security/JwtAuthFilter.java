package com.keyin.ticketnestbackend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter is a filter that intercepts incoming HTTP requests to validate the JWT token.
 * It checks the "Authorization" header for a Bearer token, validates it, and if valid,
 * sets the authentication in the SecurityContextHolder for the current request.
 * This allows Spring Security to recognize the user and their roles for authorization purposes.
 */
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * The JwtUtil is a utility class that provides methods for validating
     * JWT tokens and extracting information from them.
     */
    private final JwtUtil jwtUtil;

    /**
     * The CustomUserDetailsService is a service that loads user details from the database.
     */
    private final CustomUserDetailsService userDetailsService;

    /**
     * Constructor for JwtAuthFilter that takes a JwtUtil and CustomUserDetailsService as parameters.
     * @param jwtUtil the JwtUtil to validate JWT tokens and extract information from them.
     * @param uds the CustomUserDetailsService to load user details from
     *            the database based on the username extracted from the token.
     */
    public JwtAuthFilter(JwtUtil jwtUtil, CustomUserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = uds;
    }

    /**
     * This method is called for each incoming HTTP request.
     * It checks the "Authorization" header
     * for a Bearer token, validates it, and if valid,
     * sets the authentication in the SecurityContextHolder.
     * @param request the HttpServletRequest object that contains the request the client made to the server.
     * @param response the HttpServletResponse object that contains the response the server sends to the client.
     * @param chain the FilterChain object that allows the request and response to be passed to the next filter in the chain.
     * @throws ServletException if an error occurs during the processing of the request.
     * @throws IOException if an error occurs while processing the request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String h = request.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            String token = h.substring(7);
            try {
                String username = jwtUtil.validateAndGetUsername(token);
                UserDetails user = userDetailsService.loadUserByUsername(username);
                var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception ignored) {
            }
        }
        chain.doFilter(request, response);
    }
}
