package com.keyin.ticketnestbackend.rest.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.payment.Payment;
import com.keyin.ticketnestbackend.rest.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a booking made by a user for a specific event.
 * A booking contains the number of tickets and total price.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    /**
     * Unique identifier for the booking
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Booking number (human-readable identifier)
     */
    @NotNull(message = "Booking number is required")
    @Column(nullable = false, unique = true)
    private String bookingNumber;

    /**
     * Number of tickets booked
     */
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Date and time the booking was created
     */
    @NotNull(message = "Booking date is required")
    @Column(nullable = false)
    private LocalDateTime bookingDate;

    /**
     * Total price calculated based on quantity and event price
     */
    @NotNull(message = "Total price is required")
    @Positive(message = "Total price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Many bookings belong to one user.
     */
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Many bookings belong to one event.
     */
    @NotNull(message = "Event is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * One booking has one associated payment.
     */
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;
}