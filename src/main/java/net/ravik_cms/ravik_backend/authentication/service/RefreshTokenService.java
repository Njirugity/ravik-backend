package net.ravik_cms.ravik_backend.authentication.service;

import net.ravik_cms.ravik_backend.authentication.dto.RefreshTokenResult;

import java.util.UUID;

public interface RefreshTokenService {
    String issue(UUID userId);

    RefreshTokenResult rotate(String rawToken);

    void revoke(String rawToken);

    /**
     * Revokes every still-active refresh token for a user — used when a password change should
     * force re-login on all other sessions/devices.
     *
     * @param userId the user whose sessions should be ended
     */
    void revokeAllForUser(UUID userId);

    /**
     * Revokes every still-active refresh token for a user except the one presented — used when a
     * password change should force re-login everywhere else while keeping the caller's own
     * session alive.
     *
     * @param userId          the user whose other sessions should be ended
     * @param exceptRawToken  the raw refresh token belonging to the session to keep
     */
    void revokeAllForUserExcept(UUID userId, String exceptRawToken);
}
