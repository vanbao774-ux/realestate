package com.example.realestate.auth;

public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresInSeconds) {

    public static AuthResponse bearer(String accessToken, String refreshToken, long expiresInSeconds) {
        return new AuthResponse(accessToken, refreshToken, "Bearer", expiresInSeconds);
    }
}
