package com.inventory_management.authentication.dto;

public record AuthenticationResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {

    public static AuthenticationResponse of(String accessToken, long expiresInSeconds) {
        return new AuthenticationResponse(accessToken, "Bearer", expiresInSeconds);
    }
}
