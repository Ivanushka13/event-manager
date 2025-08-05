package ru.davydov.eventmanger.location;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationDto(
        Long id,
        @NotBlank(message = "Location name should not be blank")
        String name,
        @NotBlank(message = "Location address should not be blank")
        String address,
        @NotNull
        @Min(value = 5, message = "Minimum location capacity is 5")
        Long capacity,
        String description
) {
}
