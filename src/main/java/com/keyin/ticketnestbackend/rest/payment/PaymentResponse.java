package com.keyin.ticketnestbackend.rest.payment;

import com.keyin.ticketnestbackend.rest.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data transfer object for returning payment details along with the associated customer's name.
 *
 * @param id the unique identifier of the payment
 * @param amountPaid the amount paid by the user
 * @param status the status of the payment (e.g., SUCCESS, FAIL)
 * @param paymentDate the date and time when the payment was made
 * @param bookingId the unique identifier of the associated booking
 * @param bookingNumber the booking number associated with the payment
 * @param userId the unique identifier of the user who made the payment
 * @param userName the name of the user who made the payment
 */
public record PaymentResponse(
        Long id,
        BigDecimal amountPaid,
        PaymentStatus status,
        LocalDateTime paymentDate,
        Long bookingId,
        String bookingNumber,
        Long userId,
        String userName
) {}
