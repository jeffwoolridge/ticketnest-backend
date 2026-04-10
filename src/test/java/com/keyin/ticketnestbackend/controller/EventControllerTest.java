package com.keyin.ticketnestbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.model.Role;
import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import com.keyin.ticketnestbackend.security.JwtUtil;
import com.keyin.ticketnestbackend.security.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * EventControllerTest is a test class for the EventController in the TicketNest backend application.
 * It uses Spring Boot's testing framework to perform integration tests on the REST API endpoints
 * related to event management. The tests cover creating, retrieving, updating, and deleting events,
 * ensuring that the API behaves as expected under various conditions. The class is annotated with
 * @SpringBootTest to load the application context and @AutoConfigureMockMvc
 * to set up MockMvc for HTTP request testing. The @Transactional annotation ensures that each test
 * runs in a transaction that is rolled back after the test, keeping the database clean for subsequent tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

    /**
     * MockMvc is a Spring component that allows us to perform HTTP requests in our tests without starting a server.
     * It provides a fluent API for building requests and asserting responses,
     * making it easier to test our REST controllers.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * EventRepository is a Spring Data JPA repository for managing Event entities in the database.
     * It provides methods for saving, retrieving, updating, and deleting events,
     * which we use in our tests to set up test data and verify the results of our API calls.
     */
    @Autowired
    private EventRepository eventRepository;

    /**
     * UserRepository is a Spring Data JPA repository for managing User entities in the database.
     * It provides methods for saving, retrieving, updating, and deleting users,
     * which we use in our tests to set up test users and verify authentication and authorization.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * PasswordEncoder is a Spring Security component that provides methods for encoding and verifying passwords.
     * We use it in our tests to encode the passwords of our test users before saving them to the database,
     * ensuring that our authentication tests work correctly with the encoded passwords.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * JwtUtil is a utility class for generating and validating JSON Web Tokens (JWTs)
     * used for authentication in the application.
     * We use it in our tests to generate a valid JWT for our test users,
     * allowing us to authenticate our API requests and test the security of our endpoints.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * AuthenticationManager is a Spring Security component that manages the authentication process.
     * We use it in our tests to authenticate our test users and generate JWTs for them,
     * allowing us to test the security of our API endpoints and ensure that only authorized users can access them.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * CustomUserDetailsService is a Spring Security service that
     * loads user-specific data during the authentication process.
     * We use it in our tests to load our test users and verify that they can be
     * authenticated correctly, ensuring that our security configuration is working as expected.
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * ObjectMapper is a Jackson component that provides methods for converting Java objects to JSON and vice versa.
     * We use it in our tests to serialize our Event objects into JSON when making API
     * requests and to deserialize JSON responses from the API back into Java objects for verification.
     */
    private ObjectMapper objectMapper;

    /**
     * userToken is a String that holds the JWT token for our test user. We generate this token in the setUp method
     * using the JwtUtil class, and we use it in our API requests to authenticate as the test user.
     * This allows us to test the security of our API endpoints and ensure that only authorized users can access them.
     */
    private String userToken;

    /**
     * regularUser is a User entity that represents a regular user in our tests. We create this user in the setUp method
     * and save it to the database using the UserRepository. We use this user to authenticate our API requests and test
     * the behavior of our endpoints when accessed by a regular user, as opposed to an admin user.
     * This helps us ensure that our security configuration is correctly enforcing role-based access control on our API endpoints.
     */
    private User regularUser;

    /**
     * setUp is a method annotated with @BeforeEach, which means it runs before each test method in this class.
     * In this method, we perform several setup tasks:
     * - We clear the Event and User repositories to ensure a clean state for each test.
     * - We create a regular user with the USER role and save it to the database.
     * - We create an admin user with the ADMIN role and save it to the database.
     * - We generate a JWT token for the admin user, which we will use to authenticate our API requests in the tests.
     * - We initialize the ObjectMapper and configure it to handle Java 8 date and time types correctly,
     * ensuring that our JSON serialization and deserialization works as expected in our tests.
     */
    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        regularUser = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("user123"))
                .firstName("Regular")
                .lastName("User")
                .role(Role.USER)
                .build();
        regularUser = userRepository.save(regularUser);

        User adminUser = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .build();
        userRepository.save(adminUser);

        userToken = jwtUtil.generateToken("admin@test.com");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * createEvent is a helper method that creates and returns a new Event object with the
     * specified title and some default values for the other fields.
     * @param title the title of the event to create.
     *              This allows us to easily create events with different titles in our tests while keeping the other fields consistent.
     * @return a new Event object with the specified title and default values for the other fields.
     */
    private Event createEvent(String title) {
        return Event.builder()
                .title(title)
                .description("Test description")
                .price(new BigDecimal("50.00"))
                .date(LocalDate.of(2026, 12, 31))
                .time(LocalTime.of(20, 0))
                .totalTickets(100)
                .availableTickets(100)
                .location("Test Location")
                .build();
    }

    /**
     * createEvent_Success is a test method that verifies the successful creation of an event through the API.
     * It performs a POST request to the /api/events endpoint with valid event data and checks that the response
     * has a status of 201 Created and contains the expected event details in the JSON response body.
     * This test ensures that the API correctly handles the creation of new events when provided with valid input data.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc request or JSON processing.
     */
    @Test
    @DisplayName("POST /api/events - should create event with valid data")
    void createEvent_Success() throws Exception {
        Event event = createEvent("Test Concert");

        mockMvc.perform(post("/api/events")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Concert"))
                .andExpect(jsonPath("$.price").value(50.00))
                .andExpect(jsonPath("$.totalTickets").value(100));
    }

    /**
     * getAllEvents_Success is a test method that verifies the successful retrieval of all events through the API.
     * It first creates and saves an event in the database, then performs a GET request to the /api/events endpoint
     * and checks that the response has a status of 200 OK and contains a JSON array with the expected event details.
     * This test ensures that the API correctly returns a list of all events when requested, and that the event data
     * is properly serialized in the JSON response.
     * @throws Exception if any error occurs during the execution of the test,
     *          such as issues with the MockMvc request or JSON processing.
     */
    @Test
    @DisplayName("GET /api/events - should return all events")
    void getAllEvents_Success() throws Exception {
        Event event = createEvent("Concert 1");
        eventRepository.save(event);

        mockMvc.perform(get("/api/events")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Concert 1"));
    }

    /**
     * getEventById_Success is a test method that verifies the successful retrieval of a specific event by its ID through the API.
     * It first creates and saves an event in the database, then performs a GET request to the /api/events/{id}
     * endpoint using the ID of the created event and checks that the response has a status of 200 OK
     * and contains the expected event details in the JSON response body.
     * This test ensures that the API correctly returns the details of a specific event when requested by its ID,
     * and that the event data is properly serialized in the JSON response.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc request or JSON processing.
     */
    @Test
    @DisplayName("GET /api/events/{id} - should return event by id")
    void getEventById_Success() throws Exception {
        Event event = createEvent("Test Concert");
        event = eventRepository.save(event);

        mockMvc.perform(get("/api/events/" + event.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Concert"))
                .andExpect(jsonPath("$.id").value(event.getId()));
    }

    /**
     * updateEvent_Success is a test method that verifies the successful update of an existing event through the API.
     * It first creates and saves an event in the database, then constructs an updated Event object with new values for the fields.
     * It performs a PUT request to the /api/events/{id} endpoint using the ID of the created event and the updated event data in the request body.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc request or JSON processing.
     */
    @Test
    @DisplayName("PUT /api/events/{id} - should update event")
    void updateEvent_Success() throws Exception {
        Event event = createEvent("Original Name");
        event = eventRepository.save(event);

        Event updated = Event.builder()
                .title("Updated Name")
                .description("Updated Description")
                .price(new BigDecimal("75.00"))
                .date(LocalDate.of(2026, 11, 30))
                .time(LocalTime.of(19, 0))
                .totalTickets(150)
                .availableTickets(150)
                .location("Updated Location")
                .build();

        mockMvc.perform(put("/api/events/" + event.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(75.00))
                .andExpect(jsonPath("$.totalTickets").value(150));
    }

    /**
     * deleteEvent_Success is a test method that verifies the successful deletion of an existing event through the API.
     * It first creates and saves an event in the database, then performs a DELETE request to the /api/events/{id}
     * endpoint using the ID of the created event and checks that the response has a status of 204 No Content, indicating that the event was successfully deleted.
     * This test ensures that the API correctly handles the deletion of events when requested by their ID, and that the event is removed from the database as expected.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc request or JSON processing.
     */
    @Test
    @DisplayName("DELETE /api/events/{id} - should delete event")
    void deleteEvent_Success() throws Exception {
        Event event = createEvent("Test Concert");
        event = eventRepository.save(event);

        mockMvc.perform(delete("/api/events/" + event.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }
}