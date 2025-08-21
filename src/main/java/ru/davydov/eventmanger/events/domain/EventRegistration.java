package ru.davydov.eventmanger.events.domain;

public record EventRegistration (
        Long id,
        Long userId,
        Long eventId
){
}
