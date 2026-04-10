package com.keyin.ticketnestbackend.rest.model;

/**
 * Represents the role of a user in the system.
 */
public enum Role {
    /**
     * USER role represents a regular user with limited permissions, such as booking events and viewing their bookings.
     */
    USER,

    /**
     * ADMIN role represents an administrator with elevated permissions, such as managing events, users, and bookings.
     */
    ADMIN
}