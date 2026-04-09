package com.keyin.ticketnestbackend.rest.event;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing event-related operations.
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Constructs an EventService with the required repository.
     *
     * @param eventRepository repository used for event persistence
     */
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Creates a new event.
     * availableTickets is initialized to totalTickets.
     *
     * @param event the event to create
     * @return the saved event
     */
    public Event createEvent(Event event) {
        boolean exists = eventRepository.existsByTitleAndDateAndTimeAndLocation(
                event.getTitle(),
                event.getDate(),
                event.getTime(),
                event.getLocation()
        );

        if (exists) {
            throw new IllegalArgumentException("An event with the same title, date, time, and location already exists.");
        }

        event.setAvailableTickets(event.getTotalTickets());
        return eventRepository.save(event);
    }

    /**
     * Retrieves an event by ID.
     *
     * @param id the event ID
     * @return the found event
     * @throws IllegalArgumentException if event is not found
     */
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
    }

    /**
     * Retrieves all events.
     *
     * @return list of events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Updates an existing event.
     *
     * @param id the ID of the event to update
     * @param updatedEvent the updated event data
     * @return the updated event
     * @throws IllegalArgumentException if event is not found
     */
    public Event updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = getEventById(id);

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setTime(updatedEvent.getTime());
        existingEvent.setPrice(updatedEvent.getPrice());
        existingEvent.setTotalTickets(updatedEvent.getTotalTickets());

        return eventRepository.save(existingEvent);
    }

    /**
     * Deletes an event by ID.
     *
     * @param id the ID of the event to delete
     * @throws IllegalArgumentException if event is not found
     */
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }
}