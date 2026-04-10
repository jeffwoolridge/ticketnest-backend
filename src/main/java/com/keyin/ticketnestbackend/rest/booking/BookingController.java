package com.keyin.ticketnestbackend.rest.booking;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.booking.BookingService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for handling booking-related HTTP requests.
 * Provides endpoints for creating, retrieving, and deleting bookings.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    /**
     * Service for managing booking operations
     */
    @Autowired
    private BookingService bookingService;

    /**
     * Creates a new booking for a user and event with the specified quantity of tickets.
     * @param userId ID of the user making the booking
     * @param eventId ID of the event being booked
     * @param quantity Number of tickets to book (must be positive)
     * @return ResponseEntity containing the created booking
     */
    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestParam Long userId,
            @RequestParam Long eventId,
            @RequestParam @NotNull @Positive Integer quantity
    ) {
        Booking booking = bookingService.createBooking(userId, eventId, quantity);
        return ResponseEntity.ok(booking);
    }

    /**
     * Retrieves a booking by its ID.
     * @param id ID of the booking to retrieve
     * @return ResponseEntity containing the found booking
     */
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    /**
     * Retrieves all bookings in the system.
     * @return ResponseEntity containing a list of all bookings
     */
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Retrieves all bookings made by a specific user.
     * @param userId ID of the user whose bookings to retrieve
     * @return ResponseEntity containing a list of bookings made by the specified user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }

    /**
     * Deletes a booking by its ID, effectively canceling the booking.
     * @param id ID of the booking to delete
     * @return ResponseEntity with no content if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
