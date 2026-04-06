package com.keyin.ticketnestbackend.rest.booking;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.payment.Payment;
import com.keyin.ticketnestbackend.rest.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a booking made by a user for a specific event.
 * A booking contains the number of tickets and total price.
 */
@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    /** Unique identifier for the booking */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Number of tickets booked */
    @Column(nullable = false)
    private Integer quantity;

    /** Date and time the booking was created */
    @Column(nullable = false)
    private LocalDateTime bookingDate;

    /** Total price calculated based on quantity and event price */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Many bookings belong to one user.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Many bookings belong to one event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * One booking has one associated payment.
     */
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;
}