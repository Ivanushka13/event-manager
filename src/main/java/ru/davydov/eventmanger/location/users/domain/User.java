package ru.davydov.eventmanger.location.users.domain;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role,
        String hashedPassword
) {
}
