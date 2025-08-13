package ru.davydov.eventmanger.location.users.api;

import ru.davydov.eventmanger.location.users.domain.UserRole;

public record UserDto (
        Long id,
        String login,
        Integer age,
        UserRole role
){
}
