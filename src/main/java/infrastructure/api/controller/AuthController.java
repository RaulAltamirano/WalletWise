package infrastructure.api.controller;

import application.usecases.AuthenticateUserUseCase;
import application.usecases.RefreshTokenUseCase;
import application.usecases.RegisterUserUseCase;
import application.dtos.AuthRequest;
import application.dtos.TokenResponse;
import domain.model.User;
import infrastructure.security.JwtTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${app.cors.allowed-origins}")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final JwtTokenService jwtTokenService;

    public AuthController(
            AuthenticateUserUseCase authenticateUserUseCase,
            RegisterUserUseCase registerUserUseCase,
            RefreshTokenUseCase refreshTokenUseCase,
            JwtTokenService jwtTokenService) {
        this.authenticateUserUseCase = authenticateUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthRequest request) {
        logger.debug("Attempting login for user: {}", request.username());

        User user = authenticateUserUseCase.authenticate(request.username(), request.password());

        String accessToken = jwtTokenService.generateToken(user.getUsername());
        String refreshToken = jwtTokenService.generateToken(user.getUsername());

        logger.info("User {} successfully logged in", request.username());

        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
//        try {
//            String newAccessToken = refreshTokenUseCase.refreshAccessToken(request.refreshToken());
//            return ResponseEntity.ok(new TokenResponse(newAccessToken, null));
//        } catch (Exception e) {
//            logger.error("Error refreshing token: {}", e.getMessage());
//            throw new SecurityException("Invalid or expired refresh token");
//        }
//    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody AuthRequest request) {
        logger.debug("Attempting to register user: {}", request.username());
        User createdUser = registerUserUseCase.register(request.username(), request.password());

        logger.info("User {} successfully registered", createdUser);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // Implementar la lógica de invalidación del token si es necesario
        return ResponseEntity.noContent().build();
    }
}
