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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    private ObjectMapper objectMapper;
    private String userToken;
    private User regularUser;

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

    private Event createEvent(String title) {
        return Event.builder()
                .title(title)
                .description("Test description")
                .price(new BigDecimal("50.00"))
                .date(LocalDate.of(2026, 12, 31))
                .time(LocalTime.of(20, 0))
                .totalTickets(100)
                .availableTickets(100)
                .Location("Test Location")
                .build();
    }

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
                .Location("Updated Location")
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