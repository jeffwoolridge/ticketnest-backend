package com.keyin.ticketnestbackend.controller;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.booking.BookingRepository;
import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.model.PaymentStatus;
import com.keyin.ticketnestbackend.rest.model.Role;
import com.keyin.ticketnestbackend.rest.payment.Payment;
import com.keyin.ticketnestbackend.rest.payment.PaymentRepository;
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
 * PaymentControllerTest is a test class for the PaymentController in the TicketNest backend application.
 * It uses Spring Boot's testing framework to perform integration tests on the payment-related endpoints.
 * The tests cover creating a payment, retrieving payments, and deleting a payment.
 * Each test sets up the necessary data and verifies the expected outcomes using MockMvc to simulate HTTP requests and
 * responses.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerTest {

    /**
     * MockMvc is used to perform HTTP requests in the tests.
     * It allows us to simulate requests to the controller endpoints and verify the responses without starting a
     * full HTTP server.
     * We will use it to test the payment endpoints by sending requests and checking the status codes and
     * response content.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * PaymentRepository is used to interact with the payment data in the database during the tests.
     * We will use it to set up test data and verify the state of the database after performing operations through the
     * controller endpoints.
     * It allows us to save, retrieve, and delete payment records as needed for the tests.
     */
    @Autowired
    private PaymentRepository paymentRepository;

    /**
     * BookingRepository is used to interact with the booking data in the database during the tests.
     * We will use it to set up test data for bookings, which are necessary for creating payments,
     * and to verify the state of the database after performing operations through the controller endpoints.
     */
    @Autowired
    private BookingRepository bookingRepository;

    /**
     * EventRepository is used to interact with the event data in the database during the tests.
     * We will use it to set up test data for events, which are necessary for creating bookings and payments,
     * and to verify the state of the database after performing operations through the controller endpoints.
     */
    @Autowired
    private EventRepository eventRepository;

    /**
     * UserRepository is used to interact with the user data in the database during the tests.
     * We will use it to set up test data for users, which are necessary for creating bookings and payments,
     * and to verify the state of the database after performing operations through the controller endpoints.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * PasswordEncoder is used to encode user passwords during the setup of test data.
     * We will use it to encode the password of the test user before saving it to the database,
     * ensuring that the authentication process works correctly when generating JWT tokens for the tests.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * JwtUtil is used to generate JWT tokens for authentication during the tests.
     * We will use it to create a token for the test user, which will be included in the Authorization header
     * of the HTTP requests to authenticate the user and access the protected endpoints in the PaymentController.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * AuthenticationManager is used to authenticate users during the tests. We will use it to authenticate the test user
     * when generating JWT tokens, ensuring that the authentication process is properly tested and that the tokens
     * generated are valid for accessing the protected endpoints in the PaymentController.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * CustomUserDetailsService is used to load user details during the authentication process in the tests.
     * We will use it to load the test user's details when generating JWT tokens, ensuring that the authentication
     * process is properly tested and that the tokens generated are valid for accessing the protected endpoints
     * in the PaymentController.
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * userToken is a string that holds the JWT token for the test user. It will be generated in the setup method
     * and used in the Authorization header of the HTTP requests to authenticate the user when accessing the
     * protected endpoints in the PaymentController.
     */
    private String userToken;

    /**
     * regularUser is a User entity that represents a regular user in the tests. It will be created and saved to the
     * database in the setup method, and its details will be used to generate the JWT token for authentication during
     * the tests. This user will be associated with bookings and payments in the tests to verify the functionality of
     * the PaymentController endpoints that require user authentication and authorization.
     */
    private User regularUser;

    /**
     * testEvent is an Event entity that represents a test event in the tests. It will be created and saved to the
     * database in the setup method, and its details will be used to create bookings and payments in the tests. This
     * event will be associated with bookings and payments in the tests to verify the functionality of the
     * PaymentController endpoints that require event information for creating and retrieving payments.
     */
    private Event testEvent;

    /**
     * testBooking is a Booking entity that represents a test booking in the tests. It will be created and saved to the
     * database in the setup method, and its details will be used to create payments in the tests. This booking will be
     * associated with payments in the tests to verify the functionality of the PaymentController endpoints that
     * require booking information for creating and retrieving payments.
     */
    private Booking testBooking;

    /**
     * setUp is a method annotated with @BeforeEach that runs before each test method in the class.
     * It is used to set up the necessary data and state for the tests. In this method, we clear the relevant
     * repositories to ensure a clean state, create and save a regular user, a test event, and a test booking.
     * We also generate a JWT token for the test user that will be used for authentication in the tests.
     * This setup ensures that each test starts with a consistent and known state, allowing us to accurately
     * verify the functionality of the PaymentController endpoints.
     */
    @BeforeEach
    void setUp() {
        paymentRepository.deleteAll();
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

        testBooking = Booking.builder()
                .bookingNumber("BK001")
                .quantity(2)
                .bookingDate(LocalDateTime.now())
                .totalPrice(new BigDecimal("200.00"))
                .user(regularUser)
                .event(testEvent)
                .build();
        testBooking = bookingRepository.save(testBooking);

        userToken = jwtUtil.generateToken("user@test.com");
    }

    /**
     * createPayment_Success is a test method that verifies the successful creation of a payment
     * using the POST /api/payments endpoint.
     * It sends a POST request with valid data, including the booking ID and the amount paid, and expects a
     * 201 Created status in the response. The test also checks that the response contains the expected fields,
     * such as the payment ID, amount paid, and status. This test ensures that the PaymentController correctly
     * handles the creation of a payment when provided with valid input data and that it returns the appropriate
     * response structure and status code.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc request
     *      or response handling.
     */
    @Test
    @DisplayName("POST /api/payments - should create payment with valid data")
    void createPayment_Success() throws Exception {
        mockMvc.perform(post("/api/payments")
                        .header("Authorization", "Bearer " + userToken)
                        .param("bookingId", testBooking.getId().toString())
                        .param("amountPaid", "200.00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amountPaid").value(200.00))
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    /**
     * getAllPayments_Success is a test method that verifies the successful retrieval of all payments
     * using the GET /api/payments endpoint.
     * It first creates and saves a payment in the database, then sends a GET request to retrieve all payments.
     * The test expects a 200 OK status in the response and checks that the response contains a list of payments
     * with the expected size and content, such as the amount paid. This test ensures that the PaymentController
     * correctly handles the retrieval of all payments and that it returns the appropriate response structure and
     * status code when there are payments available in the database.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc
     * request or response handling.
     */
    @Test
    @DisplayName("GET /api/payments - should return all payments")
    void getAllPayments_Success() throws Exception {
        Payment payment = Payment.builder()
                .amountPaid(new BigDecimal("200.00"))
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .booking(testBooking)
                .build();
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/payments")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amountPaid").value(200.00));
    }

    /**
     * getPaymentById_Success is a test method that verifies the successful retrieval of a payment by its ID
     * using the GET /api/payments/{id} endpoint. It first creates and saves a payment in the database,
     * then sends a GET request with the payment ID to retrieve that specific payment. The test expects a 200 OK
     * status in the response and checks that the response contains the expected fields, such as the payment ID and
     * amount paid. This test ensures that the PaymentController correctly handles the retrieval of a payment by its
     * ID and that it returns the appropriate response structure and status code when a valid payment ID is provided.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc
     *      request or response handling.
     */
    @Test
    @DisplayName("GET /api/payments/{id} - should return payment by id")
    void getPaymentById_Success() throws Exception {
        Payment payment = Payment.builder()
                .amountPaid(new BigDecimal("200.00"))
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .booking(testBooking)
                .build();
        payment = paymentRepository.save(payment);

        mockMvc.perform(get("/api/payments/" + payment.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId()))
                .andExpect(jsonPath("$.amountPaid").value(200.00));
    }

    /**
     * getPaymentsByUser_Success is a test method that verifies the successful retrieval of payments for a specific user
     * using the GET /api/payments/user/{userId} endpoint. It first creates and saves a payment associated with the test
     * user in the database, then sends a GET request with the user ID to retrieve the payments for that user.
     * The test expects a 200 OK status in the response and checks that the response contains a list of payments with
     * the expected size and content, such as the amount paid. This test ensures that the PaymentController correctly
     * handles the retrieval of payments for a specific user and that it returns the appropriate response structure and
     * status code when a valid user ID is provided. It also verifies that the payments returned are correctly
     * associated with the specified user.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc
     *          request or response handling.
     */
    @Test
    @DisplayName("GET /api/payments/user/{userId} - should return user's payments")
    void getPaymentsByUser_Success() throws Exception {
        Payment payment = Payment.builder()
                .amountPaid(new BigDecimal("200.00"))
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .booking(testBooking)
                .build();
        paymentRepository.save(payment);

        mockMvc.perform(get("/api/payments/user/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].amountPaid").value(200.00));
    }

    /**
     * deletePayment_Success is a test method that verifies the successful deletion of a payment using the
     * DELETE /api/payments/{id} endpoint. It first creates and saves a payment in the database, then sends a
     * DELETE request with the payment ID to delete that specific payment. The test expects a 204 No Content
     * status in the response, indicating that the deletion was successful. This test ensures that the
     * PaymentController correctly handles the deletion of a payment by its ID and that it returns the appropriate
     * status code when a valid payment ID is provided. It also verifies that the payment is removed from the
     * database after the deletion operation.
     * @throws Exception if any error occurs during the execution of the test, such as issues with the MockMvc
     *          request or response handling.
     */
    @Test
    @DisplayName("DELETE /api/payments/{id} - should delete payment")
    void deletePayment_Success() throws Exception {
        Payment payment = Payment.builder()
                .amountPaid(new BigDecimal("200.00"))
                .status(PaymentStatus.SUCCESS)
                .paymentDate(LocalDateTime.now())
                .booking(testBooking)
                .build();
        payment = paymentRepository.save(payment);

        mockMvc.perform(delete("/api/payments/" + payment.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }
}