package com.keyin.ticketnestbackend.controller;

import com.keyin.ticketnestbackend.rest.auth.AuthController;
import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import com.keyin.ticketnestbackend.security.AppUserDetails;
import com.keyin.ticketnestbackend.security.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(com.keyin.ticketnestbackend.rest.auth.AuthController.class)
@Import(com.keyin.ticketnestbackend.config.JwtSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private com.keyin.ticketnestbackend.security.CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/auth/register should create user when email not taken")
    void registerCreatesUser() throws Exception {
        when(userRepository.existsByEmail(eq("bob@example.com"))).thenReturn(false);

        User saved = User.builder()
                .id(2L)
                .email("bob@example.com")
                .firstName("Bob")
                .lastName("B")
                .password("encoded")
                .role(com.keyin.ticketnestbackend.rest.model.Role.USER)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(saved);
        when(passwordEncoder.encode(eq("secret"))).thenReturn("encoded");

        mockMvc.perform(post("/api/auth/register").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"bob@example.com\",\"password\":\"secretpassword\",\"firstName\":\"Bob\",\"lastName\":\"B\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @DisplayName("POST /api/auth/login should return token and user")
    void loginReturnsTokenAndUser() throws Exception {
        User user = User.builder()
                .id(3L)
                .email("carol@example.com")
                .firstName("Carol")
                .lastName("C")
                .password("encoded")
                .role(com.keyin.ticketnestbackend.rest.model.Role.USER)
                .build();

        AppUserDetails details = new AppUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken(eq("carol@example.com"))).thenReturn("dummy-token");

        mockMvc.perform(post("/api/auth/login").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"carol@example.com\",\"password\":\"pw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"))
                .andExpect(jsonPath("$.user.email").value("carol@example.com"))
                .andExpect(jsonPath("$.user.id").value(3));
    }
}
