package net.ravik_cms.ravik_backend.common.exception;

/**
 * Thrown by the password-reset flow when a presented OTP is wrong, expired, already used, has
 * exceeded its attempt limit, or when reset-password is called before validate-otp has succeeded.
 */
public class InvalidOtpException extends RuntimeException {
    public InvalidOtpException(String message) {
        super(message);
    }
}
