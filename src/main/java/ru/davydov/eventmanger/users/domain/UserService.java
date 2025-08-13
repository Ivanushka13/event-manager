package ru.davydov.eventmanger.users.domain;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.davydov.eventmanger.users.db.UserEntityMapper;
import ru.davydov.eventmanger.users.db.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserEntityMapper userEntityMapper;

    public User saveUser(User user) {
        log.info("Save user: {}", user);
        var entity = userEntityMapper.toEntity(user);
        var savedUser = userRepository.save(entity);
        return userEntityMapper.toDomain(savedUser);
    }

    public boolean isUserExistsByLogin(String login) {
        return userRepository.findByLogin(login)
                .isPresent();
    }

    public User getUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User wasn't found by login=%s"
                        .formatted(login)));
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(userEntityMapper::toDomain)
                .orElseThrow(() -> new EntityNotFoundException("User wasn't found by id=%d"
                        .formatted(userId)));
    }

}
