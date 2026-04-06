package com.keyin.ticketnestbackend.rest.payment;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.booking.BookingRepository;
import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for managing payment-related operations.
 * Handles payment validation and event ticket updates after successful payment.
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    /**
     * Constructs a PaymentService with required repositories.
     *
     * @param paymentRepository repository used for payment persistence
     * @param bookingRepository repository used for booking lookups
     * @param eventRepository repository used for event updates
     */
    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            EventRepository eventRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * Creates a payment for a booking.
     * Payment succeeds only if the amount paid exactly matches the booking total price.
     * On successful payment, the event's available tickets are reduced.
     *
     * @param bookingId the booking ID
     * @param amountPaid the amount entered by the user
     * @return the created payment
     * @throws IllegalArgumentException if booking is not found,
     *                                  if payment already exists,
     *                                  or if payment amount is invalid
     */
    @Transactional
    public Payment createPayment(Long bookingId, BigDecimal amountPaid) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new IllegalArgumentException("Payment already exists for this booking.");
        }

        validatePaymentAmount(booking.getTotalPrice(), amountPaid);

        Event event = booking.getEvent();

        // Reduce available tickets only after successful payment
        event.setAvailableTickets(event.getAvailableTickets() - booking.getQuantity());
        eventRepository.save(event);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmountPaid(amountPaid);
        payment.setPaymentDate(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Retrieves a payment by ID.
     *
     * @param id the payment ID
     * @return the found payment
     * @throws IllegalArgumentException if payment is not found
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    /**
     * Retrieves all payments.
     *
     * @return list of all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Retrieves all payments for a given user.
     *
     * @param userId the user ID
     * @return list of the user's payments
     */
    public List<Payment> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByBookingUserId(userId);
    }

    /**
     * Deletes a payment by ID.
     *
     * @param id the payment ID
     * @throws IllegalArgumentException if payment is not found
     */
    public void deletePayment(Long id) {
        Payment payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }

    /**
     * Validates that the entered payment amount exactly matches the booking total price.
     *
     * @param totalPrice the expected booking total
     * @param amountPaid the amount entered by the user
     * @throws IllegalArgumentException if the amount does not match
     */
    public void validatePaymentAmount(BigDecimal totalPrice, BigDecimal amountPaid) {
        if (amountPaid == null || amountPaid.compareTo(totalPrice) != 0) {
            throw new IllegalArgumentException("Payment amount must exactly match the booking total price.");
        }
    }
}