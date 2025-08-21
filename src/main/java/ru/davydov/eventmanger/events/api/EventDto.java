package ru.davydov.eventmanger.events.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.davydov.eventmanger.events.domain.EventStatus;

import java.time.LocalDateTime;

public record EventDto(
        @NotNull(message = "Id is mandatory")
        Long id,

        @NotNull(message = "Name is mandatory")
        String name,

        @NotNull(message = "Owner id is mandatory")
        Long ownerId,

        @Positive(message = "Maximum places must be greater than zero")
        int maxPlaces,

        @PositiveOrZero(message = "Occupied places must me non-negative")
        int occupiedPlaces,

        @NotNull(message = "Date is mandatory")
        LocalDateTime date,

        @PositiveOrZero(message = "Cost must be non-negative")
        int cost,

        @Min(value = 30, message = "Duration must be at least 30 minutes")
        int duration,

        @NotNull(message = "Location id is mandatory")
        Long locationId,

        @NotNull(message = "Event status is mandatory")
        EventStatus status
) {
}
