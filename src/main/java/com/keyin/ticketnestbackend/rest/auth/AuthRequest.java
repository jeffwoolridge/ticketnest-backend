package com.keyin.ticketnestbackend.rest.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data transfer object for authentication requests, containing the user's email and password.
 * @param email the user's email address, which must be a valid email format and cannot be blank
 * @param password the user's password, which cannot be blank
 */
public record AuthRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
                String email,

        @NotBlank(message = "Password is required")
        String password
) {}
