package ru.davydov.eventmanger.location.users.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.davydov.eventmanger.location.users.domain.AuthenticationService;
import ru.davydov.eventmanger.location.users.domain.User;
import ru.davydov.eventmanger.location.users.domain.UserRegistrationService;
import ru.davydov.eventmanger.location.users.domain.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserRegistrationService userRegistrationService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        log.info("Get request for sign-up: login={}", signUpRequest.login());
        User user = userRegistrationService.registerUser(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertDomainUser(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ) {
        log.info("Get request for authenticate user: login={}", signInRequest.login());
        String token = authenticationService.authenticateUser(signInRequest);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable Long userId
    ) {
        log.info("Get request for get user by id: userId={}", userId);
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(convertDomainUser(user));
    }

    private UserDto convertDomainUser(User user) {
        return new UserDto(
                user.id(),
                user.login(),
                user.age(),
                user.role()
        );
    }
}
