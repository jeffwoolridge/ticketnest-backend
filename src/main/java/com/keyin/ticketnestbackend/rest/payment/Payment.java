package com.keyin.ticketnestbackend.rest.payment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.model.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a payment made for a booking.
 * A booking is only considered successful after a successful payment.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    /**
     * Unique identifier for the payment
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Amount paid by the user
     */
    @NotNull(message = "Amount paid is required")
    @Positive(message = "Amount paid must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amountPaid;

    /**
     * Status of the payment (SUCCESS or FAIL)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    /**
     * Date and time the payment was made
     */
    @NotNull(message = "Payment date is required")
    @Column(nullable = false)
    private LocalDateTime paymentDate;

    /**
     * Each payment is linked to one booking.
     * This is the owning side of the one-to-one relationship.
     */
    @JsonIgnore
    @NotNull(message = "Booking is required")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;
}
