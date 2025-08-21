package ru.davydov.eventmanger.events.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.davydov.eventmanger.events.domain.Event;
import ru.davydov.eventmanger.events.domain.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventDtoMapper eventDtoMapper;

    @PostMapping
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventCreateRequestDto eventCreateRequestDto
    ) {
        log.info("Get request for create new event: request={}", eventCreateRequestDto);
        Event createdEvent = eventService.createEvent(eventCreateRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventDtoMapper.toDto(createdEvent));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEventById(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for search event by id={}", eventId);
        Event foundEvent = eventService.getEventById(eventId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoMapper.toDto(foundEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> cancelEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for cancel event by id={}", eventId);
        eventService.cancelEvent(eventId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid EventUpdateRequestDto updateRequest
    ) {
        log.info("Get request for update event: eventId={}, request={}", eventId, updateRequest);

        var updatedEvent = eventService.updateEvent(eventId, updateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventDtoMapper.toDto(updatedEvent));
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEvents(
            @RequestBody @Valid EventSearchFilter searchFilter
    ) {
        log.info("Get request for search events by search filter: filter={}", searchFilter);

        var foundEvents = eventService.searchByFilters(searchFilter);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundEvents.stream()
                        .map(eventDtoMapper::toDto)
                        .toList()
                );
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getUserEvents() {
        log.info("Get request for get all user events");
        var events = eventService.getCurrentUserEvents();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(events.stream()
                        .map(eventDtoMapper::toDto)
                        .toList()
                );
    }

}
