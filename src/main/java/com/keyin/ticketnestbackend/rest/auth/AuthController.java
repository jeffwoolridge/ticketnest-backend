package com.keyin.ticketnestbackend.rest.auth;

import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import com.keyin.ticketnestbackend.security.JwtUtil;
import com.keyin.ticketnestbackend.security.AppUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for handling authentication-related endpoints such as registration and login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * User repository for accessing user data.
     */
    private final UserRepository userRepository;

    /**
     * Password encoder for hashing user passwords.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentication manager for authenticating user credentials during login.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * JWT utility for generating and validating JSON Web Tokens for authenticated users.
     */
    private final JwtUtil jwtUtil;

    /**
     * Constructs an AuthController with the required dependencies.
     * @param userRepository repository for user data access
     * @param passwordEncoder encoder for hashing passwords
     * @param authenticationManager manager for authenticating user credentials
     * @param jwtUtil utility for generating and validating JWT tokens
     */
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Endpoint for user registration. Validates the request,
     * checks for existing email, and creates a new user.
     *
     * @param req the registration request containing user details
     * @return a response entity with the registration result
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            return ResponseEntity.badRequest().body(Map.of("error","Email exists."));
        }
        User u = User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .firstName(req.firstName())
                .lastName(req.lastName())
                .role(com.keyin.ticketnestbackend.rest.model.Role.USER)
                .build();
        User saved = userRepository.save(u);
        return ResponseEntity.ok(Map.of("status","ok","id", saved.getId()));
    }

    /**
     * Endpoint for user login. Authenticates the user credentials
     * and returns a JWT token if successful.
     * @param req the authentication request containing email and password
     * @return a response entity with the authentication result,
     *          including a JWT token and user details if successful
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email(), req.password())
            );

            AppUserDetails ud = (AppUserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(ud.getUsername());

            Map<String,Object> resp = Map.of(
                    "token", token,
                    "user", Map.of(
                            "id", ud.getUser().getId(),
                            "firstName", ud.getUser().getFirstName(),
                            "lastName", ud.getUser().getLastName(),
                            "email", ud.getUser().getEmail(),
                            "role", ud.getUser().getRole().name()
                    )
            );

            return ResponseEntity.ok(resp);

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }
}