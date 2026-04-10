package com.keyin.ticketnestbackend.rest.booking;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Booking entities.
 * Provides methods for CRUD operations and custom queries related to bookings.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all bookings made by a specific user.
     * @param userId ID of the user whose bookings are to be retrieved
     * @return list of bookings associated with the given user ID
     */
    List<Booking> findByUserId(Long userId);
}

