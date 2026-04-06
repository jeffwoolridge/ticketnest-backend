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

class EventServiceTest {

    private final EventRepository eventRepository = mock(EventRepository.class);
    private final EventService eventService = new EventService(eventRepository);

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

    @Test
    void getEventById_returnsEvent() {
        Event e = new Event();
        e.setId(1L);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(e));

        Event result = eventService.getEventById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getEventById_throwsWhenNotFound() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> eventService.getEventById(99L));
    }
}
