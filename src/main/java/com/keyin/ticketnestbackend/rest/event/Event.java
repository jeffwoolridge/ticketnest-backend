package com.keyin.ticketnestbackend.rest.event;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String title;

    /** Detailed description of the event */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    /** Date of the event */
    @Column(nullable = false)
    private LocalDate date;

    /** Time of the event */
    @Column(nullable = false)
    private LocalTime time;

    /** Price per ticket */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** Total number of tickets for the event */
    @Column(nullable = false)
    private Integer totalTickets;

    /** Remaining tickets available for booking */
    @Column(nullable = false)
    private Integer availableTickets;

    /**
     * One event can have many bookings.
     * Each booking is linked to a specific event.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();
}