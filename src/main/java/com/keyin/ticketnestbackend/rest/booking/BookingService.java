package com.keyin.ticketnestbackend.rest.booking;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing booking-related operations.
 * Handles booking creation, price calculation, and availability checks.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    /**
     * Constructs a BookingService with required repositories.
     *
     * @param bookingRepository repository used for booking persistence
     * @param userRepository repository used for user lookups
     * @param eventRepository repository used for event lookups
     */
    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EventRepository eventRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Creates a booking for a user and event.
     * Validates ticket availability and calculates total price.
     *
     * @param userId the ID of the user making the booking
     * @param eventId the ID of the event being booked
     * @param quantity the number of tickets requested
     * @return the created booking
     * @throws IllegalArgumentException if user or event is not found,
     *                                  if quantity is invalid,
     *                                  or if available tickets are insufficient
     */
    @Transactional
    public Booking createBooking(Long userId, Long eventId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Booking quantity must be greater than 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));

        validateAvailableTickets(event, quantity);

        BigDecimal totalPrice = calculateTotalPrice(event.getPrice(), quantity);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setQuantity(quantity);
        booking.setBookingDate(LocalDateTime.now());
        booking.setTotalPrice(totalPrice);

        return bookingRepository.save(booking);
    }

    /**
     * Retrieves a booking by ID.
     *
     * @param id the booking ID
     * @return the found booking
     * @throws IllegalArgumentException if booking is not found
     */
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
    }

    /**
     * Retrieves all bookings.
     *
     * @return list of all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Retrieves all bookings for a given user.
     *
     * @param userId the user ID
     * @return list of user's bookings
     */
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    /**
     * Deletes a booking by ID.
     *
     * @param id the booking ID
     * @throws IllegalArgumentException if booking is not found
     */
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        bookingRepository.delete(booking);
    }

    /**
     * Calculates the total price for a booking.
     *
     * @param ticketPrice the price of one ticket
     * @param quantity the number of tickets
     * @return the total booking price
     */
    public BigDecimal calculateTotalPrice(BigDecimal ticketPrice, Integer quantity) {
        return ticketPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Validates that an event has enough tickets available.
     *
     * @param event the event being checked
     * @param quantity the requested number of tickets
     * @throws IllegalArgumentException if available tickets are insufficient
     */
    public void validateAvailableTickets(Event event, Integer quantity) {
        if (event.getAvailableTickets() < quantity) {
            throw new IllegalArgumentException("Not enough tickets available for this event.");
        }
    }
}