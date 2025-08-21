package ru.davydov.eventmanger.events.db;

import org.springframework.stereotype.Component;
import ru.davydov.eventmanger.events.domain.Event;
import ru.davydov.eventmanger.events.domain.EventRegistration;

@Component
public class EventEntityMapper {

    public Event toDomain(EventEntity eventEntity) {
        return new Event(
                eventEntity.getId(),
                eventEntity.getName(),
                eventEntity.getOwnerId(),
                eventEntity.getMaxPlaces(),
                eventEntity.getRegistrationList().stream()
                        .map(registration -> new EventRegistration(
                                registration.getId(),
                                registration.getUserId(),
                                eventEntity.getId())
                        )
                        .toList(),
                eventEntity.getDate(),
                eventEntity.getCost(),
                eventEntity.getDuration(),
                eventEntity.getLocationId(),
                eventEntity.getStatus()
        );
    }

}
