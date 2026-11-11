package net.ravik_cms.ravik_backend.authentication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body for {@code POST /change-password}: the logged-in self-service password change.
 *
 * @param terminateOtherSessions when true, revokes every other refresh token the user holds; the
 *                               session identified by {@code refreshToken} (if given) is kept alive
 * @param refreshToken           the caller's own current refresh token, so that session can be
 *                               exempted from revocation; optional — if omitted, every session
 *                               including the caller's own is revoked
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Current password is required")
        String currentPassword,
        @NotBlank(message = "New password is required")
        String newPassword,
        boolean terminateOtherSessions,
        String refreshToken
) {
}
