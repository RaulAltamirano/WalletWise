package application.dtos;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {}

