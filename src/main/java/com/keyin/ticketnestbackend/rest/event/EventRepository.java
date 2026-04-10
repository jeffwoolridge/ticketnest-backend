package com.keyin.ticketnestbackend.rest.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Repository interface for managing Event entities.
 */
public interface EventRepository extends JpaRepository<Event, Long> {
    /**
     * Finds an event by its title.
     *
     * @param title the title of the event to search for
     * @return the Event with the specified title, or null if not found
     */
    Event findByTitle(String title);

    /**
     * Checks if an event exists with the given title.
     *
     * @param title the title of the event to check for existence
     * @return true if an event with the specified title exists, false otherwise
     */
    boolean existsByTitle(String title);

    /**
     * Checks if an event exists with the given title, date, and time.
     * @param title the title of the event
     * @param date the date of the event
     * @param time the time of the event
     * @return true if an event with the specified title, date, and time exists, false otherwise
     */
    boolean existsByTitleAndDateAndTime(String title, LocalDate date, LocalTime time);

    /**
     * Checks if an event exists with the given title, date, time, and location.
     * @param title the title of the event
     * @param date the date of the event
     * @param time the time of the event
     * @param location the location of the event
     * @return true if an event with the specified title, date, time, and location exists, false otherwise
     */
    boolean existsByTitleAndDateAndTimeAndLocation(
            String title,
            LocalDate date,
            LocalTime time,
            String location
    );
}