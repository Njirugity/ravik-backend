package net.ravik_cms.ravik_backend.authentication.dto;

import lombok.Builder;

/**
 * Returned by {@code /login} and {@code /refresh}: the access token plus the refresh token and
 * the access token's lifetime, so the client knows when to proactively refresh.
 */
@Builder
public record LoginResponse(
        String token,
        String refreshToken,
        long expiresIn
) {
}
