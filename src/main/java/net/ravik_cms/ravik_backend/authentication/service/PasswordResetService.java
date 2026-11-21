package net.ravik_cms.ravik_backend.authentication.service;

/**
 * Logged-out forgot-password flow: request an OTP by email, validate it, then set a new password.
 */
public interface PasswordResetService {
    /**
     * Issues a fresh OTP for the account with the given email and emails it, if such an account
     * exists. Always completes without signaling whether the email was found, to avoid
     * account enumeration.
     *
     * @param email the account to send a reset code to
     */
    void forgotPassword(String email);

    /**
     * Validates a presented OTP against the user's live (unused) code.
     *
     * @param email the account the code was issued to
     * @param otp      the code the user entered
     */
    void validateOtp(String email, String otp);

    /**
     * Sets a new password for a user whose OTP has already been validated.
     *
     * @param email                  the account to reset
     * @param newPassword            the new password, checked against the strength policy
     * @param terminateOtherSessions when true, revokes every refresh token the user currently holds
     */
    void resetPassword(String email, String newPassword, boolean terminateOtherSessions);
}
