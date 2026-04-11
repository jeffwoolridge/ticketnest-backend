package com.keyin.ticketnestbackend.config;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventRepository;
import com.keyin.ticketnestbackend.rest.model.Role;
import com.keyin.ticketnestbackend.rest.user.User;
import com.keyin.ticketnestbackend.rest.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * This component seeds Event data into database on application startup.
 * It checks for existing events to avoid duplicates and adds a
 * predefined set of events if they do not already exist.
 *
 */
@Component
@RequiredArgsConstructor
public class EventDataSeeder implements CommandLineRunner {

    /**
     * A repository for managing Event entities,
     * used to check for existing events and save new ones.
     */
    private final EventRepository eventRepository;

    /**
     * A repository for managing User entities,
     * used to seed admin user into the database.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * A repository for managing User entities,
     * used to seed admin user into the database.
     */
    private final UserRepository userRepository;

    /**
     * Runs the data seeding process on application startup.
     * @param args incoming main method arguments
     */
    @Override
    public void run(String... args) {
        try {
            seedAdmin();
            seedEvents();
        } catch (Exception e) {
            System.err.println("Error seeding data: " + e.getMessage());
        }
    }

    /**
     * Seeds the database with default events if they do not already exist.
     */
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

    /**
     * Seeds the database with admin user
     */
    private void seedAdmin() {
        List<User> adminUser = List.of(
                User.builder()
                        .email("admin@ticketnest.org")
                        .firstName("Admin")
                        .lastName("TicketNest")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build()

        );

        for (User user : adminUser) {
            boolean exists = userRepository.existsByEmailAndFirstNameAndLastNameAndRoleAndPassword(
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getRole(),
                    user.getPassword()
            );

            if (!exists) {
                userRepository.save(user);
            }
        }
    }
}