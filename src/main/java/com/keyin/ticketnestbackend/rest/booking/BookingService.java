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

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final PaymentRepository paymentRepository;

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

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

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

    public BigDecimal calculateTotalPrice(BigDecimal ticketPrice, Integer quantity) {
        return ticketPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void validateAvailableTickets(Event event, Integer quantity) {
        if (event.getAvailableTickets() < quantity) {
            throw new IllegalArgumentException("Not enough tickets available for this event.");
        }
    }
}
