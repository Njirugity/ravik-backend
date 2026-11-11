package net.ravik_cms.ravik_backend.authentication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body for {@code POST /reset-password}: sets a new password for a user whose OTP was already
 * validated via {@code /validate-otp}. No OTP here — that's checked once and the record's
 * {@code verifiedAt} is what this call looks for.
 *
 * @param terminateOtherSessions when true, revokes every refresh token the user currently holds
 */
public record ResetPasswordRequest(
        @NotBlank(message = "Username is required")
        String userName,
        @NotBlank(message = "New password is required")
        String newPassword,
        boolean terminateOtherSessions
) {
}
