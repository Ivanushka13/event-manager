package ru.davydov.eventmanger.location.users.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultUserInitializer {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initUsers() {
        createUserIfNotExists("admin", "admin", UserRole.ADMIN);
        createUserIfNotExists("user", "user", UserRole.USER);
    }

    private void createUserIfNotExists(
            String login,
            String password,
            UserRole role
    ) {
        if(userService.isUserExistsByLogin(login)) {
            return;
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(
                null,
                login,
                (int)(Math.random() * 10 + 18),
                role,
                hashedPassword
        );
    }

}
