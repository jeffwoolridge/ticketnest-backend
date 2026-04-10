package com.keyin.ticketnestbackend.rest.payment;

import com.keyin.ticketnestbackend.rest.booking.Booking;
import com.keyin.ticketnestbackend.rest.booking.BookingRepository;
import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PaymentServiceTest is a unit test class for the PaymentService. It uses Mockito to mock the dependencies of the
 * PaymentService, such as the PaymentRepository, BookingRepository, and EventRepository. The tests verify that the
 * createPayment method behaves correctly under different scenarios, such as successfully creating a payment when the
 * amount matches the booking's total price and failing when there is a mismatch. The tests check that the payment's
 * amount paid, status, and the event's available tickets are updated correctly based on the booking's quantity and the
 * payment amount. The tests also ensure that the repositories are interacted with as expected during the payment
 * creation process.
 */
class PaymentServiceTest {

    /**
     * Mocks the PaymentRepository, BookingRepository, and EventRepository to test the PaymentService without relying on
     * a real database. The paymentService is initialized with the mocked repositories, allowing us to verify
     * interactions and return controlled responses for the tests. This setup enables us to test the createPayment
     * method in isolation, ensuring that it behaves correctly when creating payments based on the booking's total price
     * and the provided payment amount.
     */
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);

    /**
     * Mocks the BookingRepository to provide controlled responses when the PaymentService queries for a booking by ID.
     */
    private final BookingRepository bookingRepository = mock(BookingRepository.class);

    /**
     * Mocks the EventRepository to allow the PaymentService to update the event's available tickets when a payment is
     * created successfully. This mock enables us to verify that the event's available tickets are reduced correctly
     * based on the booking's quantity when a payment is made.
     */
    private final EventRepository eventRepository = mock(EventRepository.class);

    /**
     * The PaymentService instance being tested. It is initialized with the mocked PaymentRepository, BookingRepository,
     * and EventRepository, allowing us to test the service's methods in isolation. The tests will verify that the
     * createPayment method correctly interacts with the repositories to create a payment, update the event's available
     * tickets, and set the payment status based on the amount paid compared to the booking's total price.
     */
    private final PaymentService paymentService =
            new PaymentService(paymentRepository, bookingRepository, eventRepository);

    /**
     * Tests that a payment can be successfully created when the provided amount matches the booking's total price.
     * The test sets up a Booking with a total price and an associated Event with available tickets. It mocks the
     * repositories to return the booking and ensure that no existing payment exists for the booking. The test then
     * calls the createPayment method with the booking ID and the correct amount, and verifies that the payment's amount
     * paid is set to the provided amount, the payment status is SUCCESS, and the event's available tickets are reduced
     * by the booking's quantity. This test ensures that the createPayment method behaves correctly when the payment is
     * successful and that the event's available tickets are updated accordingly.
     */
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
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(event.getAvailableTickets()).isEqualTo(8); // 10 - quantity(2)
    }

    /**
     * Tests that the createPayment method fails when the provided amount does not match the booking's total price. The
     * test sets up a Booking with a total price and mocks the repositories to return the booking and ensure that no
     * existing payment exists for the booking. The test then calls the createPayment method with the booking ID and an
     * incorrect amount, and verifies that the payment's status is set to FAIL. This test ensures that the createPayment
     * method correctly identifies when the payment amount does not match the booking's total price and sets the payment
     * status to FAIL accordingly.
     */
    @Test
    void createPayment_failsIfAmountMismatch() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTotalPrice(BigDecimal.TEN);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(paymentRepository.findByBookingId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.createPayment(1L, BigDecimal.ONE);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAIL);
    }
}
