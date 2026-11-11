package net.ravik_cms.ravik_backend.authentication.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Body for {@code POST /validate-otp}: the code just emailed to the user, checked before
 * {@code reset-password} will accept a new password.
 */
public record ValidateOtpRequest(
        @NotBlank(message = "Username is required")
        String userName,
        @NotBlank(message = "OTP is required")
        String otp
) {
}
