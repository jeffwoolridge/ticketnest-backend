package com.keyin.ticketnestbackend.rest.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data transfer object for user registration requests,
 * containing the user's email, password, first name, and last name.
 * @param email the user's email address, which must be a valid email format and cannot be blank
 * @param password the user's password, which must be at least 8 characters long and cannot be blank
 * @param firstName the user's first name, which cannot be blank and must be less than 50 characters
 * @param lastName the user's last name, which cannot be blank and must be less than 50 characters
 */
public record RegisterRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Enter a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be less than 50 characters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be less than 50 characters")
        String lastName
) {}