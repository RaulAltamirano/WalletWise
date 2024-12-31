package application.usecases;

import domain.model.User;
import domain.repository.UserRepository;
import infrastructure.security.JwtTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

public class RegisterUserUseCase {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(String username, String password) {
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(null, username, hashedPassword, Set.of("USER"));
        userRepository.save(user);
    }
}

public class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    public AuthenticateUserUseCase(UserRepository userRepository, JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public String authenticate(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        return jwtTokenService.generateAccessToken(user);
    }
}
