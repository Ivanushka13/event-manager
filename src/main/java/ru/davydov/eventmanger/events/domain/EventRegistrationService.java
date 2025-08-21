package ru.davydov.eventmanger.events.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.davydov.eventmanger.events.db.*;
import ru.davydov.eventmanger.users.domain.User;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final EventEntityMapper eventEntityMapper;

    public void registerUserOnEvent(
            User user,
            Long eventId
    ) {
        Event event = eventService.getEventById(eventId);
        if(user.id().equals(event.ownerId())) {
            throw new IllegalArgumentException("Owner cannot register on his event");
        }

        var registration = registrationRepository.findRegistration(user.id(), eventId);
        if(registration.isPresent()) {
            throw new IllegalArgumentException("User already registered on event");
        }

        if(!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot register on event with status: status=%s"
                    .formatted(event.status()));
        }

        registrationRepository.save(
                new EventRegistrationEntity(
                        null,
                        user.id(),
                        eventRepository.findById(eventId).orElseThrow()
                )
        );
    }

    public void cancelUserRegistration(
            User user,
            Long eventId
    ) {
        Event event = eventService.getEventById(eventId);

        var registration = registrationRepository.findRegistration(user.id(), eventId);
        if(registration.isEmpty()) {
            throw new IllegalArgumentException("User have not registered on event");
        }

        if(!event.status().equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot cancel event with status: status=%s"
                    .formatted(event.status()));
        }

        registrationRepository.delete(registration.orElseThrow());
    }

    public List<Event> getUserRegisteredEvents(Long userId) {
        var foundEvents = registrationRepository.findRegisteredEvents(userId);

        return foundEvents.stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }

}
