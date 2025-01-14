package com.free.WalletWise.infrastructure.api.controller;

import com.free.WalletWise.application.dtos.UserDTO;
import com.free.WalletWise.application.usecases.AuthenticateUserUseCase;
import com.free.WalletWise.application.usecases.RefreshTokenUseCase;
import com.free.WalletWise.application.usecases.RegisterUserUseCase;
import com.free.WalletWise.application.dtos.AuthRequest;
import com.free.WalletWise.domain.exceptions.InvalidPasswordException;
import com.free.WalletWise.domain.model.User;
import com.free.WalletWise.infrastructure.security.JwtTokenService;
import com.free.WalletWise.interfaces.ApiResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/public/auth")
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
    public ResponseEntity<String> login(@RequestBody Map<String, String> request) {
        logger.debug("Attempting login for user: {}", request.get("username"));
        return ResponseEntity.ok("Login successful for " + request.get("username"));
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
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody AuthRequest request) {
        try {
            User user = registerUserUseCase.register(request.username(), request.password());
            UserDTO userDTO = UserDTO.from(user);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(userDTO, "User registered successfully"));

        } catch (InvalidPasswordException e) {
            logger.warn("Invalid password attempt: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(e.getMessage()));

        } catch (Exception e) {
            logger.error("Registration error", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // Implementar la lógica de invalidación del token si es necesario
        return ResponseEntity.noContent().build();
    }
}
