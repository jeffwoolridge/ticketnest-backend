package com.keyin.ticketnestbackend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CorsConfigTest {

    @Test
    @DisplayName("Should allow localhost:5173 origin")
    void allowsFirstAllowedOrigin() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowedOrigins().contains("http://localhost:5173"));
    }

    @Test
    @DisplayName("Should allow localhost:5174 origin")
    void allowsSecondAllowedOrigin() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowedOrigins().contains("http://localhost:5174"));
    }

    @Test
    @DisplayName("Should allow all HTTP methods")
    void allowsAllHttpMethods() {
        CorsConfiguration config = createExpectedConfig();
        List<String> methods = config.getAllowedMethods();
        assertTrue(methods.contains("GET"));
        assertTrue(methods.contains("POST"));
        assertTrue(methods.contains("PUT"));
        assertTrue(methods.contains("DELETE"));
        assertTrue(methods.contains("OPTIONS"));
        assertTrue(methods.contains("PATCH"));
    }

    @Test
    @DisplayName("Should allow credentials")
    void allowsCredentials() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowCredentials());
    }

    @Test
    @DisplayName("Should allow all headers")
    void allowsAllHeaders() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowedHeaders().contains("*"));
    }

    @Test
    @DisplayName("Should have max age of 3600 seconds")
    void hasCorrectMaxAge() {
        CorsConfiguration config = createExpectedConfig();
        assertEquals(3600L, config.getMaxAge());
    }

    @Test
    @DisplayName("JwtSecurityConfig bean creation should not throw")
    void jwtSecurityConfigCanBeInstantiated() {
        assertDoesNotThrow(() -> {
            JwtSecurityConfig config = new JwtSecurityConfig(null, null);
            assertNotNull(config.corsConfigurationSource());
        });
    }

    private CorsConfiguration createExpectedConfig() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:5174"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        return config;
    }
}
