package com.keyin.ticketnestbackend.config;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventDataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;

    @Override
    public void run(String... args) {
        seedEvents();
    }

    private void seedEvents() {
        List<Event> defaultEvents = List.of(
                Event.builder()
                        .title("Tech Conference 2026")
                        .description("A conference for developers and tech enthusiasts.")
                        .date(LocalDate.of(2026, 5, 20))
                        .time(LocalTime.of(10, 0))
                        .price(new BigDecimal("49.99"))
                        .totalTickets(200)
                        .availableTickets(200)
                        .location("St. John's Convention Centre")
                        .build(),

                Event.builder()
                        .title("Music Night Live")
                        .description("Live music event with guest artists.")
                        .date(LocalDate.of(2026, 6, 10))
                        .time(LocalTime.of(19, 30))
                        .price(new BigDecimal("35.00"))
                        .totalTickets(150)
                        .availableTickets(150)
                        .location("Downtown Arena")
                        .build(),

                Event.builder()
                        .title("Startup Networking Meetup")
                        .description("Meet founders, developers, and investors.")
                        .date(LocalDate.of(2026, 7, 5))
                        .time(LocalTime.of(18, 0))
                        .price(new BigDecimal("15.00"))
                        .totalTickets(100)
                        .availableTickets(100)
                        .location("Innovation Hub")
                        .build()
        );

        for (Event event : defaultEvents) {
            boolean exists = eventRepository.existsByTitleAndDateAndTime(
                    event.getTitle(),
                    event.getDate(),
                    event.getTime()
            );

            if (!exists) {
                eventRepository.save(event);
            }
        }
    }
}