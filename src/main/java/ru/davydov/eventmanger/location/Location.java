package ru.davydov.eventmanger.location;

public record Location (
        Long id,
        String name,
        String address,
        Long capacity,
        String description
){
}
