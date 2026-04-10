package com.keyin.ticketnestbackend.rest.model;

/**
 * Represents the status of a payment.
 */
public enum PaymentStatus {
    /**
     * SUCCESS indicates that the payment was processed successfully.
     */
    SUCCESS,

    /**
     * FAIL indicates that the payment processing failed, which could be due to various reasons such as insufficient
     * funds, invalid payment details, or technical issues.
     */
    FAIL
}