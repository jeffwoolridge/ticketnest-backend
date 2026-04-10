package com.keyin.ticketnestbackend.config;

import com.keyin.ticketnestbackend.security.CustomUserDetailsService;
import com.keyin.ticketnestbackend.security.JwtAuthFilter;
import com.keyin.ticketnestbackend.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for setting up JWT-based security in the application.
 * It defines the security filter chain, password encoder, authentication manager,
 * and CORS configuration.
 * The class is annotated with @Configuration to indicate that it provides
 * Spring beans, and @EnableMethodSecurity to enable method-level security annotations.
 */
@Configuration
@EnableMethodSecurity
public class JwtSecurityConfig {
    /**
     * Utility class for handling JWT operations such as token generation and validation.
     */
    private final JwtUtil jwtUtil;

    /**
     * Custom user details service that loads user-specific data during authentication.
     */
    private final CustomUserDetailsService uds;

    /**
     * Constructs a JwtSecurityConfig with the required dependencies.
     * @param jwtUtil utility for JWT operations
     * @param uds custom user details service for loading user data during authentication
     */
    public JwtSecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.uds = uds;
    }

    /**
     * Defines the security filter chain for the application,
     * configuring HTTP security settings,
     * including CSRF protection, CORS configuration,
     * request authorization rules, and JWT authentication filter.
     * @param http the HttpSecurity object used to configure security settings
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtil, uds);

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/error",
                            "/favicon.ico",
                            "/actuator/health",
                            "/v3/api-docs",
                            "/api/auth/**",
                            "/api/auth",
                            "/api/bookings/**",
                            "/api/bookings",
                            "/api/events/**",
                            "/api/events",
                            "/api/payments/**",
                            "/api/payments",
                            "/api/users/**",
                            "/api/users"
                    ).permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Defines a bean for password encoding using BCryptPasswordEncoder.
     * @return a PasswordEncoder instance that uses BCrypt hashing algorithm
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines a bean for the authentication manager,
     * which is responsible for processing authentication requests.
     *
     * @param uds the UserDetailsService used to load user-specific data during authentication
     * @return an AuthenticationManager instance configured with a DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds) {
        return new ProviderManager(new org.springframework.security.authentication.dao.DaoAuthenticationProvider() {{
            setUserDetailsService(uds);
            setPasswordEncoder(passwordEncoder());
        }});
    }

    /**
     * Defines a bean for CORS configuration, allowing cross-origin requests from specified origins,
     * with allowed methods, headers, and credentials settings.
     * @return a CorsConfigurationSource that provides the CORS configuration for the application
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
