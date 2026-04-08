package com.keyin.ticketnestbackend.rest.event;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an event that users can book tickets for.
 * Each event has a limited number of available tickets.
 */
@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    /** Unique identifier for the event */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Event title */
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    private String title;

    /** Detailed description of the event */
    @NotBlank(message = "Description is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Date of the event */
    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    /** Time of the event */
    @NotNull(message = "Time is required")
    @Column(nullable = false)
    private LocalTime time;

    /** Price per ticket */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Total number of tickets for the event */
    @NotNull(message = "Total tickets is required")
    @Positive(message = "Total tickets must be greater than 0")
    @Column(nullable = false)
    private Integer totalTickets;

    /** Remaining tickets available for booking */
    @NotNull(message = "Available tickets is required")
    @PositiveOrZero(message = "Available tickets cannot be negative")
    @Column(nullable = false)
    private Integer availableTickets;

    @NotNull(message = "Location is required")
    @Column(nullable = false)
    private String Location;

    /**
     * One event can have many bookings.
     * Each booking is linked to a specific event.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
}