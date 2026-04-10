package com.keyin.ticketnestbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * TestJacksonConfig is a test configuration class that provides
 * a custom ObjectMapper bean for testing purposes.
 * This configuration ensures that the ObjectMapper used in
 * tests is properly set up to handle Java 8 date and time types
 * (like LocalDate and LocalDateTime) by registering the JavaTimeModule.
 * Additionally, it disables the default behavior of writing dates as
 * timestamps, which allows for more human-readable date formats in JSON.
 * By marking the ObjectMapper bean as @Primary, we ensure that it will
 * be used in place of any other ObjectMapper beans defined in the
 * application context during testing, allowing our tests to utilize this
 * customized configuration for JSON serialization and deserialization.
 */
@TestConfiguration
public class TestJacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}