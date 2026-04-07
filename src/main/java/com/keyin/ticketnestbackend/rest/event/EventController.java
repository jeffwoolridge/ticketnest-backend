package com.keyin.ticketnestbackend.rest.event;

import com.keyin.ticketnestbackend.rest.event.Event;
import com.keyin.ticketnestbackend.rest.event.EventService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody Event event) {
        Event savedEvent = eventService.createEvent(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedEvent);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEventById(id));
    }

    @GetMapping
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id,
                                             @Valid @RequestBody Event eventDetails) {
        return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
