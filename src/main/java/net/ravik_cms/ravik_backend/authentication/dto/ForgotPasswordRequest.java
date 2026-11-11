package net.ravik_cms.ravik_backend.authentication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body for {@code POST /forgot-password}: the account to send a reset OTP to, identified by
 * username (same field {@code /login} uses).
 */
public record ForgotPasswordRequest(
        @NotBlank(message = "Username is required")
        String userName
) {
}
