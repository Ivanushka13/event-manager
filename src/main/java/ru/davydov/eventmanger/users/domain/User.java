package ru.davydov.eventmanger.users.domain;

public record User(
        Long id,
        String login,
        Integer age,
        UserRole role,
        String hashedPassword
) {
}
