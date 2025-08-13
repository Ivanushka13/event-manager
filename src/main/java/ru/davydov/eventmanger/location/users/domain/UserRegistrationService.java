package ru.davydov.eventmanger.location.users.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.davydov.eventmanger.location.users.api.SignUpRequest;

@Service
@RequiredArgsConstructor
public class UserRegistrationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(SignUpRequest request) {
        if (userService.isUserExistsByLogin(request.login())) {
            throw new IllegalArgumentException("User with such login already exists");
        }
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                null,
                request.login(),
                request.age(),
                UserRole.USER,
                hashedPassword
        );
        return userService.saveUser(user);
    }
}
