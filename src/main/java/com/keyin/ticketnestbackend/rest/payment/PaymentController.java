package com.keyin.ticketnestbackend.rest.payment;

import com.keyin.ticketnestbackend.rest.payment.Payment;
import com.keyin.ticketnestbackend.rest.payment.PaymentService;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller class for handling payment-related API endpoints.
 * Provides endpoints for creating, retrieving, and canceling payments.
 * Each payment is associated with a booking, and a booking is only
 * considered successful after a successful payment.
 * The controller uses the PaymentService to perform business logic and data access operations.
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    /**
     * Service for managing payment operations
     */
    @Autowired
    private PaymentService paymentService;

    /**
     * Creates a new payment for a given booking.
     * @param bookingId the ID of the booking associated with the payment
     * @param amountPaid the amount paid by the user, must be a positive value
     * @return the created payment with HTTP status 201 (Created)
     */
    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long bookingId,
            @RequestParam @NotNull @Positive BigDecimal amountPaid
    ) {
        Payment payment = paymentService.createPayment(bookingId, amountPaid);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    /**
     * Retrieves payment details by payment ID, including the associated customer's name.
     * @param id the ID of the payment to retrieve
     * @return the payment details along with the customer's name,
     *      or HTTP status 404 if not found
     */
    @GetMapping("/{id}/customerName")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentsById(id));
    }

    /**
     * Retrieves payment details by payment ID without the customer's name.
     * @param id the ID of the payment to retrieve
     * @return the payment details without the customer's name,
     *      or HTTP status 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentRaw(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    /**
     * Retrieves all payments in the system.
     * @return a list of all payments with HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    /**
     * Retrieves all payments made by a specific user, identified by their user ID.
     * @param userId the ID of the user whose payments are to be retrieved
     * @return a list of payments made by the specified user with HTTP status 200 (OK)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    /**
     * Cancels a payment by its ID. This will delete the payment record from the system.
     * @param id the ID of the payment to cancel
     * @return HTTP status 204 (No Content) if the payment was successfully canceled,
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        paymentService.deletePayment(id);   // service method name is deletePayment
        return ResponseEntity.noContent().build();
    }
}
