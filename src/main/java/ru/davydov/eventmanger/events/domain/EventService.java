package ru.davydov.eventmanger.events.domain;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.davydov.eventmanger.events.api.EventCreateRequestDto;
import ru.davydov.eventmanger.events.api.EventSearchFilter;
import ru.davydov.eventmanger.events.api.EventUpdateRequestDto;
import ru.davydov.eventmanger.events.db.EventEntity;
import ru.davydov.eventmanger.events.db.EventEntityMapper;
import ru.davydov.eventmanger.events.db.EventRepository;
import ru.davydov.eventmanger.location.Location;
import ru.davydov.eventmanger.location.LocationService;
import ru.davydov.eventmanger.users.domain.AuthenticationService;
import ru.davydov.eventmanger.users.domain.User;
import ru.davydov.eventmanger.users.domain.UserRole;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final LocationService locationService;
    private final AuthenticationService authenticationService;
    private final EventEntityMapper eventEntityMapper;

    public Event createEvent(EventCreateRequestDto createRequest) {
        Location location = locationService.getLocationById(createRequest.locationId());
        if (location.capacity() < createRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Location capacity is less than max places: capacity=%s, maxPlaces=%s"
                            .formatted(createRequest.locationId(), createRequest.maxPlaces())
            );
        }

        EventEntity eventEntity = createEventEntityFromRequest(createRequest);
        eventEntity = eventRepository.save(eventEntity);

        log.info("New event was created: eventId={}", eventEntity.getId());

        return eventEntityMapper.toDomain(eventEntity);
    }

    public Event getEventById(Long eventId) {
        var event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Event entity wasn't found by id=%s"
                                .formatted(eventId))
                );

        return eventEntityMapper.toDomain(event);
    }

    public void cancelEvent(Long eventId) {
        checkCurrentUserCanModifyEvent(eventId);
        Event event = getEventById(eventId);

        if (event.status().equals(EventStatus.CANCELLED)) {
            log.info("Event has already been cancelled: eventId={}", eventId);
            return;
        }

        if (event.status().equals(EventStatus.FINISHED)
                || event.status().equals(EventStatus.STARTED)) {
            throw new IllegalArgumentException("Cannot cancel event with status: status=%s"
                    .formatted(event.status()));
        }

        eventRepository.changeEventStatus(eventId, EventStatus.CANCELLED);
    }

    private void checkCurrentUserCanModifyEvent(Long eventId) {
        User currentUser = authenticationService.getCurrentAuthenticatedUser();
        Event event = getEventById(eventId);

        if (!event.ownerId().equals(currentUser.id())
                && !currentUser.role().equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("Current user cannot modify this event");
        }
    }

    public Event updateEvent(Long eventId, EventUpdateRequestDto updateRequest) {
        checkCurrentUserCanModifyEvent(eventId);
        EventEntity event = eventRepository.findById(eventId).orElseThrow();

        checkEventStatusBeforeUpdate(event.getStatus());

        if (updateRequest.maxPlaces() != null || updateRequest.locationId() != null) {
            Long locationId = Optional.ofNullable(updateRequest.locationId())
                    .orElse(event.getLocationId());
            int maxPlaces = Optional.ofNullable(updateRequest.maxPlaces())
                    .orElse(event.getMaxPlaces());

            Location location = locationService.getLocationById(locationId);
            checkLocationCapacityIsMoreThanMaxPlaces(location.capacity(), maxPlaces);
        }

        if (updateRequest.maxPlaces() != null
                && event.getRegistrationList().size() > updateRequest.maxPlaces()) {
            throw new IllegalArgumentException(
                    "Registration count is more than maxPlaces: regCount=%s, maxPlaces=%s"
                            .formatted(event.getRegistrationList().size(), updateRequest.maxPlaces()));
        }

        checkRegistrationsCountIsMoreThanMaxPlaces(
                event.getRegistrationList().size(),
                updateRequest.maxPlaces()
        );

        updateEventFieldsFromRequest(event, updateRequest);

        event = eventRepository.save(event);

        return eventEntityMapper.toDomain(event);
    }

    private void checkEventStatusBeforeUpdate(EventStatus status) {
        if (!status.equals(EventStatus.WAIT_START)) {
            throw new IllegalArgumentException("Cannot modify event in status: %s"
                    .formatted(status));
        }
    }

    private void checkLocationCapacityIsMoreThanMaxPlaces(
            Long locationCapacity,
            int maxPlaces
    ) {
        if (locationCapacity < maxPlaces) {
            throw new IllegalArgumentException(
                    "Capacity of location less than maxPlaces: capacity=%s, maxPlaces=%s"
                            .formatted(locationCapacity, maxPlaces)
            );
        }
    }

    private void checkRegistrationsCountIsMoreThanMaxPlaces(
            int registrationsCount,
            Integer maxPlaces
    ) {
        if (maxPlaces != null && registrationsCount > maxPlaces) {
            throw new IllegalArgumentException(
                    "Registration count is more than maxPlaces: regCount=%s, maxPlaces=%s"
                            .formatted(registrationsCount, maxPlaces));
        }
    }

    private void updateEventFieldsFromRequest(
            EventEntity event,
            EventUpdateRequestDto updateRequest
    ) {
        Optional.ofNullable(updateRequest.name())
                .ifPresent(event::setName);
        Optional.ofNullable(updateRequest.maxPlaces())
                .ifPresent(event::setMaxPlaces);
        Optional.ofNullable(updateRequest.date())
                .ifPresent(event::setDate);
        Optional.ofNullable(updateRequest.cost())
                .ifPresent(event::setCost);
        Optional.ofNullable(updateRequest.duration())
                .ifPresent(event::setDuration);
        Optional.ofNullable(updateRequest.locationId())
                .ifPresent(event::setLocationId);
    }

    private EventEntity createEventEntityFromRequest(EventCreateRequestDto createRequest) {
        User currentUser = authenticationService.getCurrentAuthenticatedUser();

        return new EventEntity(
                null,
                createRequest.name(),
                currentUser.id(),
                createRequest.maxPlaces(),
                Collections.emptyList(),
                createRequest.date(),
                createRequest.cost(),
                createRequest.duration(),
                createRequest.locationId(),
                EventStatus.WAIT_START
        );
    }

    public List<Event> searchByFilters(EventSearchFilter searchFilter) {
        var foundEntities = eventRepository.findEvents(
                searchFilter.name(),
                searchFilter.placesMin(),
                searchFilter.placesMax(),
                searchFilter.dateStartAfter(),
                searchFilter.dateStartBefore(),
                searchFilter.costMin(),
                searchFilter.costMax(),
                searchFilter.durationMin(),
                searchFilter.durationMax(),
                searchFilter.locationId(),
                searchFilter.eventStatus()
        );

        return foundEntities.stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }

    public List<Event> getCurrentUserEvents() {
        User currentUser = authenticationService.getCurrentAuthenticatedUser();
        List<EventEntity> userEvents = eventRepository.findAllByOwnerId(currentUser.id());

        return userEvents.stream()
                .map(eventEntityMapper::toDomain)
                .toList();
    }
}
