package ru.davydov.eventmanger.users.api;

import ru.davydov.eventmanger.users.domain.UserRole;

public record UserDto (
        Long id,
        String login,
        Integer age,
        UserRole role
){
}
