package net.ravik_cms.ravik_backend.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Body for {@code POST /validate-otp}: the code just emailed to the user, checked before
 * {@code reset-password} will accept a new password.
 */
public record ValidateOtpRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        @NotBlank(message = "OTP is required")
        String otp
) {
}
