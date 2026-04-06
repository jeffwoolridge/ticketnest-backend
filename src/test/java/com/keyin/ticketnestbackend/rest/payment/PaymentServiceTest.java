package com.keyin.ticketnestbackend.rest.payment;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.booking.BookingRepository;
import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final EventRepository eventRepository = mock(EventRepository.class);

    private final PaymentService paymentService =
            new PaymentService(paymentRepository, bookingRepository, eventRepository);

    @Test
    void createPayment_success() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTotalPrice(BigDecimal.TEN);

        Event event = new Event();
        event.setId(1L);
        event.setAvailableTickets(10);
        booking.setEvent(event);
        booking.setQuantity(2);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.createPayment(1L, BigDecimal.TEN);

        assertThat(payment.getAmountPaid()).isEqualTo(BigDecimal.TEN);
        assertThat(event.getAvailableTickets()).isEqualTo(8); // 10 - quantity(2)
    }

    @Test
    void createPayment_throwsIfAmountMismatch() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTotalPrice(BigDecimal.TEN);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.createPayment(1L, BigDecimal.ONE));
    }
}
