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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
                .build();
        testEvent = eventRepository.save(testEvent);

        userToken = jwtUtil.generateToken("user@test.com");
    }

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