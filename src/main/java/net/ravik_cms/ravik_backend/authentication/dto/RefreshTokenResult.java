package net.ravik_cms.ravik_backend.authentication.dto;

import java.util.UUID;

/**
 * Outcome of rotating a refresh token: the owning user and the newly issued raw token.
 */
public record RefreshTokenResult(UUID userId, String rawToken) {
}
