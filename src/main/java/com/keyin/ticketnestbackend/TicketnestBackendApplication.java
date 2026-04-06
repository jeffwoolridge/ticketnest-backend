package com.keyin.ticketnestbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the RESTful service,
 * responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
public class TicketnestBackendApplication {

    /**
     * Default constructor for the RestServiceApplication class.
     * This constructor is required by Spring Boot to create an instance of the application.
     */
    public TicketnestBackendApplication() {
    }

    /**
     * The entry point of the application, which starts the Spring Boot context.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketnestBackendApplication.class, args);
    }

}
