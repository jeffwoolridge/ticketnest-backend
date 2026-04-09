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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private String userToken;
    private User regularUser;
    private Event testEvent;
    private Booking testBooking;

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