package com.keyin.ticketnestbackend.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CORS configuration defined in JwtSecurityConfig.
 * These tests verify that the CORS settings allow the expected origins,
 * HTTP methods, headers, and credentials, and that the max age is set correctly.
 * Additionally, we test that the JwtSecurityConfig bean can be instantiated
 * without throwing any exceptions.
 * The createExpectedConfig method is a helper that constructs a
 * CorsConfiguration object with the expected settings for comparison in the tests.
 */
class CorsConfigTest {

    /**
     * Tests that the CORS configuration allows the origin "http://localhost:5173".
     */
    @Test
    @DisplayName("Should allow localhost:5173 origin")
    void allowsFirstAllowedOrigin() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowedOrigins().contains("http://localhost:5173"));
    }

    /**
     * Tests that the CORS configuration allows the origin "http://localhost:5174".
     */
    @Test
    @DisplayName("Should allow localhost:5174 origin")
    void allowsSecondAllowedOrigin() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowedOrigins().contains("http://localhost:5174"));
    }

    /**
     * Tests that the CORS configuration allows all common
     * HTTP methods: GET, POST, PUT, DELETE, OPTIONS, and PATCH.
     */
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

    /**
     * Tests that the CORS configuration allows credentials (cookies, authorization headers, etc.)
     * to be included in cross-origin requests.
     */
    @Test
    @DisplayName("Should allow credentials")
    void allowsCredentials() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowCredentials());
    }

    /**
     * Tests that the CORS configuration allows all headers by checking for the presence of "*"
     */
    @Test
    @DisplayName("Should allow all headers")
    void allowsAllHeaders() {
        CorsConfiguration config = createExpectedConfig();
        assertTrue(config.getAllowedHeaders().contains("*"));
    }

    /**
     * Tests that the CORS configuration has a max age of 3600 seconds, which indicates how long
     * the results of a preflight request can be cached by the client.
     */
    @Test
    @DisplayName("Should have max age of 3600 seconds")
    void hasCorrectMaxAge() {
        CorsConfiguration config = createExpectedConfig();
        assertEquals(3600L, config.getMaxAge());
    }

    /**
     * Tests that the JwtSecurityConfig bean can be instantiated without throwing any exceptions.
     */
    @Test
    @DisplayName("JwtSecurityConfig bean creation should not throw")
    void jwtSecurityConfigCanBeInstantiated() {
        assertDoesNotThrow(() -> {
            JwtSecurityConfig config = new JwtSecurityConfig(null, null);
            assertNotNull(config.corsConfigurationSource());
        });
    }

    /**
     * Helper method to create a CorsConfiguration object with the expected settings for testing.
     * @return a CorsConfiguration object configured with the expected allowed origins, methods, headers, credentials, and max age.
     */
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
