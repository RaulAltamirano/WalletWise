package application.usecases;

import infrastructure.security.JwtTokenService;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenUseCase {

    private final JwtTokenService jwtTokenService;

    public RefreshTokenUseCase(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

//    public String refreshAccessToken(String refreshToken) {
//        // Validar el refreshToken
//        if (!jwtTokenService.isTokenValid(refreshToken, username)) {
//            throw new RuntimeException("Invalid refresh token");
//        }
//
//        // Generar un nuevo accessToken
//        return jwtTokenService.generateToken(username);
//    }

}
