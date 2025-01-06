package application.usecases;

import domain.exceptions.InvalidCredentialsException;
import domain.model.User;
import domain.repository.UserRepository;
import infrastructure.entity.UserMapper;
import infrastructure.security.JwtTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthenticateUserUseCase(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public User authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .map(userEntity -> {
                    User user = UserMapper.toDomain(userEntity);

                    if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        throw new InvalidCredentialsException("Invalid credentials");
                    }

                    return user;
                })
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
    }

}

