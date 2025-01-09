package com.free.WalletWise.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtTokenService {

    private final SecretKey signingKey;
    private final long accessTokenExpirationHours;
    private final long refreshTokenExpirationHours;

    public JwtTokenService(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token.expiration-hours:1}") long accessTokenExpirationHours,
            @Value("${jwt.refresh-token.expiration-hours:24}") long refreshTokenExpirationHours
    ) {
        if (secretKey.length() < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 characters long");
        }
        this.signingKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationHours = accessTokenExpirationHours;
        this.refreshTokenExpirationHours = refreshTokenExpirationHours;
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, accessTokenExpirationHours);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("token_type", "refresh");
        return generateToken(claims, userDetails, refreshTokenExpirationHours);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expirationHours) {
        Instant now = Instant.now();

        return Jwts.builder()
                .claims(extraClaims) // Set custom claims (new API)
                .subject(userDetails.getUsername()) // Set subject (new API)
                .issuedAt(Date.from(now)) // Set issued at time (new API)
                .expiration(Date.from(now.plus(expirationHours, ChronoUnit.HOURS))) // Set expiration time (new API)
                .signWith(signingKey, Jwts.SIG.HS256) // Sign the token with the key (new API)
                .compact(); // Compact into a URL-safe string
    }

    public Map<String, Object> validateToken(String token) {
        return Jwts.parser()
                .verifyWith(signingKey) // Verify the token with the signing key (new API)
                .build()
                .parseSignedClaims(token) // Parse the token (new API)
                .getPayload(); // Get the claims (new API)
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isAccessToken(String token) {
        try {
            return !extractClaim(token, claims ->
                    claims.getOrDefault("token_type", "").equals("refresh"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRefreshToken(String token) {
        try {
            return extractClaim(token, claims ->
                    claims.getOrDefault("token_type", "").equals("refresh"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) &&
                    !isTokenExpired(token) &&
                    userDetails.isEnabled();
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}