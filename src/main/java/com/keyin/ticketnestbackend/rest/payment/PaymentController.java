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

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestParam Long bookingId,
            @RequestParam @NotNull @Positive BigDecimal amountPaid
    ) {
        Payment payment = paymentService.createPayment(bookingId, amountPaid);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
//        return ResponseEntity.ok(paymentService.getPaymentById(id));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
//        return ResponseEntity.ok(paymentService.getPaymentById(id));
//    }

    @GetMapping("/{id}/customerName")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentsById(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentRaw(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPaymentsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(paymentService.getPaymentsByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelPayment(@PathVariable Long id) {
        paymentService.deletePayment(id);   // service method name is deletePayment
        return ResponseEntity.noContent().build();
    }
}
