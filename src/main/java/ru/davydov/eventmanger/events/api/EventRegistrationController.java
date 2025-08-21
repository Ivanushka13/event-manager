package ru.davydov.eventmanger.events.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.davydov.eventmanger.events.domain.EventRegistrationService;
import ru.davydov.eventmanger.users.domain.AuthenticationService;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
@Slf4j
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;
    private final EventDtoMapper eventDtoMapper;
    private final AuthenticationService authenticationService;

    @RequestMapping("/{eventId}")
    public ResponseEntity<Void> registerOnEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for register on event: eventId={}", eventId);

        eventRegistrationService.registerUserOnEvent(
                authenticationService.getCurrentAuthenticatedUser(),
                eventId
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegisterOnEvent(
            @PathVariable("eventId") Long eventId
    ) {
        log.info("Get request for cancel register on event: eventId={}", eventId);

        eventRegistrationService.cancelUserRegistration(
                authenticationService.getCurrentAuthenticatedUser(),
                eventId
        );

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getUserRegistrations() {
        log.info("Get request for get all user registrations");

        var foundEvents = eventRegistrationService.getUserRegisteredEvents(
                authenticationService.getCurrentAuthenticatedUser().id()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(foundEvents.stream()
                        .map(eventDtoMapper::toDto)
                        .toList()
                );
    }

}
