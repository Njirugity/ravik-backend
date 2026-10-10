package net.ravik_cms.ravik_backend.authentication.dto;

/**
 * Body shape for both {@code POST /refresh} and {@code POST /logout} — both only need the raw
 * refresh token the client is holding.
 */
public record RefreshRequest(String refreshToken) {
}
