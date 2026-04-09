package com.keyin.ticketnestbackend.rest.payment;

import com.keyin.ticketnestbackend.rest.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
