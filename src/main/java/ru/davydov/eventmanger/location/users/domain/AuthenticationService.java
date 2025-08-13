package ru.davydov.eventmanger.location.users.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.davydov.eventmanger.location.users.api.SignInRequest;
import ru.davydov.eventmanger.security.JwtTokenManager;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtTokenManager jwtTokenManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public String authenticateUser(SignInRequest request) {
        if (!userService.isUserExistsByLogin(request.login())) {
            throw new BadCredentialsException("Bad credentials");
        }
        User user = userService.getUserByLogin(request.login());
        if (!passwordEncoder.matches(request.password(), user.hashedPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        return jwtTokenManager.generateToken(user);
    }
}
