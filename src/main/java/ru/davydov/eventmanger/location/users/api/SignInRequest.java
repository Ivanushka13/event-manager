package ru.davydov.eventmanger.location.users.api;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
        @NotBlank String login,
        @NotBlank String password
) {
}
