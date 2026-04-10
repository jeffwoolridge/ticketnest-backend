package com.keyin.ticketnestbackend.rest.event;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * EventController is a REST controller that handles HTTP requests related to events. It provides endpoints for
 * creating, retrieving, updating, and deleting events. The controller uses the EventService to perform the necessary
 * business logic and interacts with the EventRepository for data persistence. The endpoints are secured with role-based
 * access control, allowing only users with the ADMIN role to create, update, or delete events, while any authenticated
 * user can retrieve event information.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    /**
     * The EventService is injected into the controller to handle the business logic related to events. It provides
     * methods for creating, retrieving, updating, and deleting events. The controller delegates the actual operations
     * to the service layer, allowing for a separation of concerns and better organization of the code. The EventService
     * interacts with the EventRepository to perform database operations, while the controller focuses on handling HTTP
     * requests and responses.
     */
    @Autowired
    private EventService eventService;

    /**
     * Endpoint to create a new event. This endpoint is secured and can only be accessed by users with the ADMIN role.
     * @param event the event object to be created, which is validated using @Valid to ensure that it meets the defined constraints.
     * @return a ResponseEntity containing the created event and an HTTP status of CREATED (201) if successful, or an
     *          error message if the event already exists or if validation fails.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    /**
     * Endpoint to retrieve an event by its ID. This endpoint can be accessed by any authenticated user. It takes the
     * event ID as a path variable and returns the corresponding event if found. If the event is not found, it throws
     * an IllegalArgumentException with a message indicating that the event was not found.
     * @param id the ID of the event to retrieve, which is passed as a path variable in the URL.
     * @return a ResponseEntity containing the retrieved event and an HTTP status of OK (200) if successful, or an
     *          error message if the event is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    /**
     * Endpoint to retrieve all events. This endpoint can be accessed by any authenticated user. It returns a list of
     * all events available in the system. The response is wrapped in a ResponseEntity with an HTTP status of OK (200).
     * If there are no events, it returns an empty list.
     * @return a ResponseEntity containing a list of all events and an HTTP status of OK (200). If there are no events,
     *      it returns an empty list.
     */
    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        System.out.println("EventController.getAllEvents()");
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    /**
     * Endpoint to update an existing event. This endpoint is secured and can only be accessed by users with the ADMIN
     * role. It takes the event ID as a path variable and the updated event details in the request body. The updated
     * event details are validated using @Valid to ensure that they meet the defined constraints. If the event is
     * successfully updated, it returns the updated event with an HTTP status of OK (200). If the event is not found,
     * it throws an IllegalArgumentException with a message indicating that the event was not found. If the updated
     * event details are invalid, it returns an error message with an appropriate HTTP status code.
     * @param id the ID of the event to update, which is passed as a path variable in the URL.
     * @param eventDetails the updated event details, which are validated using @Valid.
     * @return a ResponseEntity containing the updated event and an HTTP status of OK (200) if successful, or an
     *          error message if the event is not found or if the updated event details are invalid.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,
                                             @Valid @RequestBody Event eventDetails) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
    }

    /**
     * Endpoint to delete an event by its ID. This endpoint is secured and can only be accessed by users with the ADMIN
     * role. It takes the event ID as a path variable and deletes the corresponding event if found. If the event is
     * successfully deleted, it returns an HTTP status of NO_CONTENT (204). If the event is not found, it throws an
     * IllegalArgumentException with a message indicating that the event was not found.
     * @param id the ID of the event to delete, which is passed as a path variable in the URL.
     * @return a ResponseEntity with an HTTP status of NO_CONTENT (204) if successful, or an error message if the
     * event is not found.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
