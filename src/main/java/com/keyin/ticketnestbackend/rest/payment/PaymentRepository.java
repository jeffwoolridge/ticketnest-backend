package com.keyin.ticketnestbackend.rest.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Payment entities.
 */
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    /**
     * Finds the payment associated with a specific booking.
     *
     * @param bookingId the id of the booking
     * @return an Optional containing the payment if found
     */
    Optional<Payment> findByBookingId(Long bookingId);

    /**
     * Finds all payments made by a specific user through their bookings.
     *
     * @param userId the id of the user
     * @return list of payments associated with the user
     */
    List<Payment> findByBookingUserId(Long userId);
}
