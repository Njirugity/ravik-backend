package net.ravik_cms.ravik_backend.authentication.service;

import net.ravik_cms.ravik_backend.authentication.dto.CurrentUserDto;
import net.ravik_cms.ravik_backend.authentication.dto.LoginRequest;
import net.ravik_cms.ravik_backend.authentication.dto.LoginResponse;

import java.util.UUID;

public interface AuthService {
    CurrentUserDto getCurrentUser(UUID userId, UUID projectId);

    LoginResponse login(LoginRequest request);

    LoginResponse refresh(String refreshToken);

    void logout(String refreshToken);

    /**
     * Self-service password change for a logged-in user.
     *
     * @param userId                 the authenticated user making the change
     * @param currentPassword        their current password, verified before anything changes
     * @param newPassword            the new password, checked against the strength policy
     * @param terminateOtherSessions when true, revokes every other refresh token the user holds —
     *                               if {@code currentRefreshToken} is given, that session is kept
     *                               alive; otherwise every session, including the caller's own, is
     *                               revoked
     * @param currentRefreshToken    the caller's own refresh token, so it can be exempted from
     *                               revocation; may be {@code null}
     */
    void changePassword(UUID userId, String currentPassword, String newPassword, boolean terminateOtherSessions,
                         String currentRefreshToken);
}
