package com.keyin.ticketnestbackend.rest.event;

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
 * EventServiceTest is a unit test class for the EventService. It uses Mockito to mock the EventRepository dependency.
 * The tests verify that the createEvent method correctly sets the available tickets to the total tickets and saves the
 * event. The getEventById method is tested to ensure it returns the correct event when found and throws an exception
 * when the event is not found. The tests check that the event's available tickets are set correctly and that the
 * repository's save and findById methods are called with the expected arguments.
 */
class EventServiceTest {

    /**
     * Mocks the EventRepository to test the EventService without relying on a real database. The eventService is
     * initialized with the mocked repository, allowing us to verify interactions and return controlled responses for
     * the tests.
     */
    private final EventRepository eventRepository = mock(EventRepository.class);

    /**
     * The EventService instance being tested. It is initialized with the mocked EventRepository, allowing us to test
     * the service's methods in isolation. The tests will verify that the service correctly interacts with the
     * repository and behaves as expected when creating events and retrieving events by ID.
     */
    private final EventService eventService = new EventService(eventRepository);

    /**
     * Tests that the createEvent method sets the available tickets to the total tickets and saves the event using the
     * repository. The test creates an Event object with specific attributes, mocks the save method of the repository
     * to return the saved event, and then verifies that the available tickets are set correctly and that the save
     * method is called with the expected event object.
     */
    @Test
    void createEvent_setsAvailableTicketsAndSaves() {
        Event input = Event.builder()
                .title("Rock")
                .description("Show")
                .date(LocalDate.now())
                .time(LocalTime.NOON)
                .price(BigDecimal.TEN)
                .totalTickets(100)
                .availableTickets(0)
                .build();

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Event saved = eventService.createEvent(input);

        assertThat(saved.getAvailableTickets()).isEqualTo(100);
        verify(eventRepository).save(saved);
    }

    /**
     * Tests that the getEventById method returns the correct event when it is found in the repository. The test mocks
     * the findById method of the repository to return an Optional containing an Event with a specific ID. It then
     * calls the getEventById method and verifies that the returned event has the expected ID. This test ensures that
     * the service correctly retrieves events by their ID and that the repository's findById method is called with the
     * correct argument.
     */
    @Test
    void getEventById_returnsEvent() {
        Event e = new Event();
        e.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(e));

        Event result = eventService.getEventById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    /**
     * Tests that the getEventById method throws an IllegalArgumentException when the event is not found in the repository.
     * The test mocks the findById method of the repository to return an empty Optional when searching for an event with
     * a specific ID. It then calls the getEventById method and verifies that an IllegalArgumentException is thrown.
     * This test ensures that the service correctly handles the case where an event is not found and that it throws the
     * appropriate exception to indicate the error.
     */
    @Test
    void getEventById_throwsWhenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> eventService.getEventById(99L));
    }
}
