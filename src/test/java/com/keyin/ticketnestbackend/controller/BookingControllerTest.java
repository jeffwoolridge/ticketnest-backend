package com.keyin.ticketnestbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.booking.BookingRepository;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BookingControllerTest is a test class for the BookingController, which handles booking-related endpoints.
 * We use @SpringBootTest to load the full application context and
 * @AutoConfigureMockMvc to set up MockMvc for testing the web layer.
 * The tests cover creating a booking, retrieving bookings by various criteria, and deleting a booking.
 * We also set up test data for a regular user and an event to use in our tests.
 * Each test verifies that the endpoints return the expected status codes and
 * response content based on the operations performed.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerTest {

    /**
        * MockMvc is a Spring MVC testing tool that allows us to perform HTTP requests
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * We use the BookingRepository to set up test data and verify results in our tests.
     */
    @Autowired
    private BookingRepository bookingRepository;

    /**
     * We use the EventRepository to set up test data and verify results in our tests.
     */
    @Autowired
    private EventRepository eventRepository;

    /**
     * We use the UserRepository to set up test data and verify results in our tests.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * We use the PasswordEncoder to encode passwords for our test users,
     * ensuring that they are stored in the same way as in the production application.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * We use the JwtUtil to generate JWT tokens for authentication in our tests,
     * allowing us to test the secured endpoints of the BookingController.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * We use the AuthenticationManager to authenticate users in our tests,
     * simulating the login process and ensuring that our endpoints are properly secured.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * We use the CustomUserDetailsService to load user details for authentication in our tests,
     * allowing us to simulate the behavior of the authentication process
     * and test the security of our endpoints
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * UserToken is a JWT token that we generate for our test user to authenticate requests
     * to the secured endpoints of the BookingController.
     */
    private String userToken;

    /**
     * We set up a regular user in our tests.
     * The regular user is created with a specific email, password, and role.
     * This user is used to authenticate requests to the BookingController
     * and to associate bookings with a user in our tests.
     */
    private User regularUser;

    /**
     * We set up a test event in our tests. The event is created with a title, description, price,
     * date, time, total tickets, available tickets, and location. This event is used to create bookings
     * in our tests and to verify that the booking operations are working correctly.
     */
    private Event testEvent;

    /**
     * The setUp method is annotated with @BeforeEach, which means it will run before each test method in this class.
     * In this method, we clear the repositories to ensure a clean state for each test,
     * and we create a regular user and a test event that will be used in our tests.
     * We also generate a JWT token for the regular user to authenticate requests to the BookingController.
     * This setup ensures that each test starts with a consistent and known state,
     * allowing us to accurately test the functionality of the BookingController endpoints.
     */
    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
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

        testEvent = Event.builder()
                .title("Test Concert")
                .description("Test Description")
                .price(new BigDecimal("100.00"))
                .date(LocalDate.of(2026, 12, 31))
                .time(LocalTime.of(20, 0))
                .totalTickets(500)
                .availableTickets(500)
                .location("Test Location")
                .build();
        testEvent = eventRepository.save(testEvent);

        userToken = jwtUtil.generateToken("user@test.com");
    }

    /**
     * Test for the create booking endpoint. We simulate a POST request to /api/bookings with valid parameters
     * and an Authorization header containing the JWT token for the regular user.
     * We verify that the response status is OK and that the response body contains
     * the expected booking details, such as the quantity and total price.
     * This test ensures that the create booking functionality works correctly
     * when provided with valid data and proper authentication.
     * @throws Exception if the request fails or the assertions do not hold.
     *          We verify that if the data is valid, the controller creates a new
     *          booking and returns a successful response with the booking details.
     */
    @Test
    @DisplayName("POST /api/bookings - should create booking with valid data")
    void createBooking_Success() throws Exception {
        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + userToken)
                        .param("userId", regularUser.getId().toString())
                        .param("eventId", testEvent.getId().toString())
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalPrice").value(200.00));
    }

    /**
     * Test for the get all bookings endpoint. We first create a booking in the database using the BookingRepository,
     * and then we simulate a GET request to /api/bookings with an Authorization header containing the JWT token for the regular user.
     * We verify that the response status is OK and that the response body contains a list of bookings with the expected details, such as the quantity.
     * @throws Exception if the request fails or the assertions do not hold. We verify that if there are bookings in the database,
     *          the controller returns a successful response with a list of bookings.
     */
    @Test
    @DisplayName("GET /api/bookings - should return all bookings")
    void getAllBookings_Success() throws Exception {
        Booking booking = Booking.builder()
                .bookingNumber("BK001")
                .quantity(2)
                .bookingDate(LocalDateTime.now())
                .totalPrice(new BigDecimal("200.00"))
                .user(regularUser)
                .event(testEvent)
                .build();
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    /**
     * Test for the get booking by ID endpoint. We first create a booking in the database using the BookingRepository,
     * and then we simulate a GET request to /api/bookings/{id} with the ID of the created booking and an Authorization header containing the JWT token for the regular user.
     * @throws Exception if the request fails or the assertions do not hold. We verify that if a booking with the specified ID exists,
     *          the controller returns a successful response with the booking details.
     */
    @Test
    @DisplayName("GET /api/bookings/{id} - should return booking by id")
    void getBookingById_Success() throws Exception {
        Booking booking = Booking.builder()
                .bookingNumber("BK002")
                .quantity(3)
                .bookingDate(LocalDateTime.now())
                .totalPrice(new BigDecimal("300.00"))
                .user(regularUser)
                .event(testEvent)
                .build();
        booking = bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/" + booking.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(booking.getId()))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    /**
     * Test for the get bookings by user ID endpoint. We first create a booking in the database using the BookingRepository,
     * and then we simulate a GET request to /api/bookings/user/{userId}
     * with the ID of the regular user and an Authorization header containing the JWT token for the regular user.
     * @throws Exception if the request fails or the assertions do not hold. We verify that if there are bookings associated with the specified user ID,
     *          the controller returns a successful response with a list of bookings for that user.
     */
    @Test
    @DisplayName("GET /api/bookings/user/{userId} - should return user's bookings")
    void getBookingsByUser_Success() throws Exception {
        Booking booking = Booking.builder()
                .bookingNumber("BK003")
                .quantity(2)
                .bookingDate(LocalDateTime.now())
                .totalPrice(new BigDecimal("200.00"))
                .user(regularUser)
                .event(testEvent)
                .build();
        bookingRepository.save(booking);

        mockMvc.perform(get("/api/bookings/user/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].user.id").value(regularUser.getId()));
    }

    /**
     * Test for the delete booking endpoint. We first create a booking in the database using the BookingRepository,
     * and then we simulate a DELETE request to /api/bookings/{id} with the ID of the created booking and an
     * Authorization header containing the JWT token for the regular user.
     * @throws Exception if the request fails or the assertions do not hold. We verify that if a booking with the specified ID exists,
     *          the controller cancels the booking and returns a No Content status.
     */
    @Test
    @DisplayName("DELETE /api/bookings/{id} - should cancel booking")
    void deleteBooking_Success() throws Exception {
        Booking booking = Booking.builder()
                .bookingNumber("BK004")
                .quantity(2)
                .bookingDate(LocalDateTime.now())
                .totalPrice(new BigDecimal("200.00"))
                .user(regularUser)
                .event(testEvent)
                .build();
        booking = bookingRepository.save(booking);

        mockMvc.perform(delete("/api/bookings/" + booking.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }
}