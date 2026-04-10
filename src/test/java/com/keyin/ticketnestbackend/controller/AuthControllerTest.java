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

/**
 * AuthControllerTest is a test class for the AuthController, which handles user registration and login.
 * We use @WebMvcTest to focus on testing the web layer, and we mock the dependencies of the
 * controller such as UserRepository, PasswordEncoder, AuthenticationManager, JwtUtil, and
 * CustomUserDetailsService. The tests verify that the registration endpoint creates a
 * user when the email is not taken, and that the login endpoint returns a token and
 * user information when the credentials are correct.
 */
@WebMvcTest(com.keyin.ticketnestbackend.rest.auth.AuthController.class)
@Import(com.keyin.ticketnestbackend.config.JwtSecurityConfig.class)
public class AuthControllerTest {

    /**
     * MockMvc is a Spring MVC testing tool that allows us to perform HTTP requests
     * and assert the responses without starting a full server. We use it to test the
     * endpoints of the AuthController by simulating HTTP requests and verifying the results.
     * It is automatically configured by the @WebMvcTest annotation, and we can inject it
     * into our test class to use in our test methods.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * We mock the UserRepository because we want to isolate the tests for the AuthController.
     */
    @MockBean
    private UserRepository userRepository;

    /**
     * We mock the PasswordEncoder because we don't want to rely on the actual encoding logic in our tests.
     */
    @MockBean
    private PasswordEncoder passwordEncoder;

    /**
     * We mock the AuthenticationManager. By mocking, we can simulate their behavior and focus on testing the controller's logic.
     */
    @MockBean
    private AuthenticationManager authenticationManager;

    /**
     * We mock the JwtUtil because we want to control the token generation in our tests and avoid dependencies on the actual implementation.
     */
    @MockBean
    private JwtUtil jwtUtil;

    /**
     * We mock the CustomUserDetailsService because it is a dependency of the authentication process,
     * and we want to isolate our tests from its implementation.
     * By mocking it, we can simulate its behavior and focus on testing the AuthController's
     * logic without relying on the actual user details service.
     */
    @MockBean
    private com.keyin.ticketnestbackend.security.CustomUserDetailsService customUserDetailsService;

    /**
     * Test for the registration endpoint.
     * We simulate a POST request to /api/auth/register with a JSON payload containing
     * the user's email, password, first name, and last name.
     * @throws Exception if the request fails or the assertions do not hold.
     * We verify that if the email is not taken, the controller creates a new user and returns a successful response with the user's ID.
     */
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

    /**
     * Test for the login endpoint.
     * We simulate a POST request to /api/auth/login with a JSON payload containing the user's email and password.
     * We set up the mocks to simulate a successful authentication and token generation.
     * @throws Exception
     */
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
