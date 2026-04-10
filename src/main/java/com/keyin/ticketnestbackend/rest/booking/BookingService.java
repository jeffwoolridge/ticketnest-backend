package com.keyin.ticketnestbackend.rest.booking;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.payment.PaymentRepository;
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

    /**
     * Repositories for managing bookings, users, events, and payments
     */
    private final BookingRepository bookingRepository;

    /**
     * User repository for retrieving user information during booking creation
     */
    private final UserRepository userRepository;

    /**
     * Event repository for retrieving event information and updating available
     * tickets during booking creation and deletion
     */
    private final EventRepository eventRepository;

    /**
     * Payment repository for checking if a booking has an associated
     * payment before allowing deletion
     */
    private final PaymentRepository paymentRepository;

    /**
     * Constructs a BookingService with the required repositories.
     * @param bookingRepository repository used for booking persistence
     * @param userRepository repository used for retrieving user information during booking creation
     * @param eventRepository repository used for retrieving event information and updating available tickets during booking creation and deletion
     * @param paymentRepository repository used for checking if a booking has an associated payment before allowing deletion
     */
    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            PaymentRepository paymentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Creates a new booking for a user and event with the specified quantity of tickets.
     * @param userId ID of the user making the booking
     * @param eventId ID of the event being booked
     * @param quantity Number of tickets to book (must be positive)
     * @return the created booking
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

        String bookingNumber = "BKG-" + System.currentTimeMillis();
        booking.setBookingNumber(bookingNumber);

        return bookingRepository.save(booking);
    }

    /**
     * Retrieves a booking by its ID.
     * @param id ID of the booking to retrieve
     * @return the found booking
     * @throws IllegalArgumentException if booking is not found
     */
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
    }

    /**
     * Retrieves all bookings in the system.
     * @return list of all bookings
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Retrieves all bookings made by a specific user.
     * @param userId ID of the user whose bookings to retrieve
     * @return list of bookings made by the specified user
     */
    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    /**
     * Deletes a booking by its ID, effectively canceling the booking.
     * If the booking has an associated payment, it will not be deleted
     * and an exception will be thrown. If the booking is successfully deleted
     * and does not have an associated payment, the available tickets
     * for the event will be updated accordingly.
     *
     * @param id the ID of the booking to delete
     */
    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = getBookingById(id);
        
        boolean hasPayment = paymentRepository.findByBookingId(id).isPresent();
        
        if (!hasPayment) {
            Event event = booking.getEvent();
            event.setAvailableTickets(event.getAvailableTickets() + booking.getQuantity());
            eventRepository.save(event);
        }
        
        bookingRepository.delete(booking);
    }

    /**
     * Calculates the total price for a booking based on the ticket price and quantity.
     * @param ticketPrice the price of a single ticket for the event
     * @param quantity the number of tickets being booked
     * @return the total price for the booking, calculated as ticketPrice multiplied by quantity
     */
    public BigDecimal calculateTotalPrice(BigDecimal ticketPrice, Integer quantity) {
        return ticketPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Validates that there are enough available tickets for the event to fulfill the booking request.
     * @param event the event for which the booking is being made
     * @param quantity the number of tickets being booked
     * @throws IllegalArgumentException if the number of available tickets
     *     for the event is less than the quantity being booked,
     *     indicating that there are not enough tickets available to fulfill the booking request
     */
    public void validateAvailableTickets(Event event, Integer quantity) {
        if (event.getAvailableTickets() < quantity) {
            throw new IllegalArgumentException("Not enough tickets available for this event.");
        }
    }
}
