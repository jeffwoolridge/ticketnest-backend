package com.keyin.ticketnestbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private ObjectMapper objectMapper;
    private String adminToken;
    private String userToken;
    private User adminUser;
    private User regularUser;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        adminUser = User.builder()
                .email("admin@test.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        regularUser = User.builder()
                .email("user@test.com")
                .password(passwordEncoder.encode("user123"))
                .firstName("Regular")
                .lastName("User")
                .role(Role.USER)
                .build();
        regularUser = userRepository.save(regularUser);

        adminToken = jwtUtil.generateToken("admin@test.com");
        userToken = jwtUtil.generateToken("user@test.com");

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("POST /api/users - admin can create user")
    void createUser_Admin_Success() throws Exception {
        User newUser = User.builder()
                .email("newuser@test.com")
                .password(passwordEncoder.encode("password123"))
                .firstName("New")
                .lastName("User")
                .role(Role.USER)
                .build();

        mockMvc.perform(post("/api/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("newuser@test.com"))
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("GET /api/users - admin can get all users")
    void getAllUsers_Admin_Success() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("admin@test.com"))
                .andExpect(jsonPath("$[1].email").value("user@test.com"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - should return user by id")
    void getUserById_Success() throws Exception {
        mockMvc.perform(get("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@test.com"))
                .andExpect(jsonPath("$.firstName").value("Regular"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - user can update their own profile")
    void updateUser_Self_Success() throws Exception {
        User updated = User.builder()
                .firstName("UpdatedFirst")
                .lastName("UpdatedLast")
                .email("user@test.com")
                .password(passwordEncoder.encode("newpassword"))
                .role(Role.USER)
                .build();

        mockMvc.perform(put("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("UpdatedFirst"))
                .andExpect(jsonPath("$.lastName").value("UpdatedLast"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - admin can delete user")
    void deleteUser_Admin_Success() throws Exception {
        mockMvc.perform(delete("/api/users/" + regularUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}