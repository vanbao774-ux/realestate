package com.example.realestate.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtService {

    private final SecretKey key;
    private final long expirationHours;
    private final long refreshExpirationHours;

    public JwtService(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.expiration-hours:4}") long expirationHours,
        @Value("${app.jwt.refresh-expiration-hours:168}") long refreshExpirationHours
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationHours = expirationHours;
        this.refreshExpirationHours = refreshExpirationHours;
    }

    public String generateAccessToken(String subject, Map<String, Object> claims) {
        return generateToken(subject, claims, expirationHours);
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, Map.of(), refreshExpirationHours);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public Instant extractExpiration(String token) {
        return parseClaims(token).getExpiration().toInstant();
    }

    public long getAccessTokenTtlSeconds() {
        return Duration.ofHours(expirationHours).toSeconds();
    }

    public long getRefreshTokenTtlSeconds() {
        return Duration.ofHours(refreshExpirationHours).toSeconds();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).isBefore(Instant.now());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private String generateToken(String subject, Map<String, Object> claims, long hours) {
        Instant now = Instant.now();
        return Jwts.builder()
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(hours, ChronoUnit.HOURS)))
            .claims(claims)
            .signWith(key)
            .compact();
    }
}
