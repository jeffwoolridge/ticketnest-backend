package com.keyin.ticketnestbackend.rest.booking;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.payment.PaymentRepository;
import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * BookingServiceTest is a unit test class for the BookingService. It uses Mockito to mock the dependencies
 * of the BookingService, such as the BookingRepository, UserRepository, EventRepository, and PaymentRepository.
 * The tests verify that the createBooking method behaves correctly under different scenarios, such as successfully
 * creating a booking and throwing an exception when there are not enough tickets available. The tests check that the
 * booking is created with the correct user, event, quantity, total price, and booking number.
 */
class BookingServiceTest {

    private final BookingRepository bookingRepository = mock(BookingRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final EventRepository eventRepository = mock(EventRepository.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);

    private final BookingService bookingService =
            new BookingService(bookingRepository, userRepository, eventRepository, paymentRepository);

    /**
     * Tests that a booking can be successfully created when valid user, event, and quantity are provided.
     */
    @Test
    void createBooking_success() {
        User user = new User();
        user.setId(1L);

        Event event = Event.builder()
                .id(1L)
                .title("Rock")
                .description("Show")
                .date(LocalDate.now())
                .time(LocalTime.NOON)
                .price(BigDecimal.TEN)
                .totalTickets(100)
                .availableTickets(100)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Booking booking = bookingService.createBooking(1L, 1L, 2);

        assertThat(booking.getUser()).isEqualTo(user);
        assertThat(booking.getEvent()).isEqualTo(event);
        assertThat(booking.getQuantity()).isEqualTo(2);
        assertThat(booking.getTotalPrice()).isEqualTo(BigDecimal.valueOf(20));
        assertThat(booking.getBookingNumber()).startsWith("BKG-");
    }

    /**
     * Tests that an IllegalArgumentException is thrown when trying to create a booking with a quantity that exceeds
     * the available tickets for the event.
     */
    @Test
    void createBooking_throwsIfNotEnoughTickets() {
        User user = new User();
        user.setId(1L);

        Event event = new Event();
        event.setId(1L);
        event.setAvailableTickets(1);
        event.setPrice(BigDecimal.TEN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(1L, 1L, 5));
    }
}
