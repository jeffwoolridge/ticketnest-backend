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

@Configuration
@EnableMethodSecurity
public class JwtSecurityConfig {
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService uds;

    public JwtSecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService uds) {
        this.jwtUtil = jwtUtil;
        this.uds = uds;
    }

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
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api-docs",
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds) {
        return new ProviderManager(new org.springframework.security.authentication.dao.DaoAuthenticationProvider() {{
            setUserDetailsService(uds);
            setPasswordEncoder(passwordEncoder());
        }});
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
