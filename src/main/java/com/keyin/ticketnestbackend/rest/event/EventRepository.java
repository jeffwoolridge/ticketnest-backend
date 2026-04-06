package com.keyin.ticketnestbackend.rest.event;

import com.keyin.ticketnestbackend.rest.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Event entities.
 */
public interface EventRepository extends JpaRepository<User, Long> {
    /**
     * Finds an event by its name.
     *
     * @param name the name of the event to search for
     * @return the Event with the specified name, or null if not found
     */
    Event findByName(String name);

    /**
     * Checks if an event exists with the given name.
     *
     * @param title the title of the event to check for existence
     * @return true if an event with the specified title exists, false otherwise
     */
    boolean existsByTitle(String title);
}
