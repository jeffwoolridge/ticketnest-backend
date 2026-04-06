package com.keyin.ticketnestbackend.rest.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing Booking entities.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {
     /**
      * Finds a booking by its booking number.
      *
      * @param bookingNumber the booking number to search for
      * @return the Booking with the specified booking number, or null if not found
      */
     Booking findByBookingNumber(String bookingNumber);

     /**
      * Checks if a booking exists with the given booking number.
      *
      * @param bookingNumber the booking number to check for existence
      * @return true if a booking with the specified booking number exists, false otherwise
      */
     boolean existsByBookingNumber(String bookingNumber);

     /**
      * Finds all bookings belonging to a specific user.
      *
      * @param userId the id of the user
      * @return list of bookings for the user
      */
     List<Booking> findByUserId(Long userId);

     /**
      * Finds all bookings for a specific event.
      *
      * @param eventId the id of the event
      * @return list of bookings for the event
      */
     List<Booking> findByEventId(Long eventId);
}
