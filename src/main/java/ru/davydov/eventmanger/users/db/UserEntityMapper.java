package ru.davydov.eventmanger.users.db;

import org.springframework.stereotype.Component;
import ru.davydov.eventmanger.users.domain.User;
import ru.davydov.eventmanger.users.domain.UserRole;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.login(),
                user.age(),
                user.role().name(),
                user.hashedPassword()
        );
    }

    public User toDomain(UserEntity userEntity) {
        return new User(
                userEntity.getId(),
                userEntity.getLogin(),
                userEntity.getAge(),
                UserRole.valueOf(userEntity.getRole()),
                userEntity.getHashedPassword()
        );
    }

}
