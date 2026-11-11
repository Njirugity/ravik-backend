package net.ravik_cms.ravik_backend.authentication.service;

/**
 * Logged-out forgot-password flow: request an OTP by email, validate it, then set a new password.
 */
public interface PasswordResetService {
    /**
     * Issues a fresh OTP for the given username and emails it, if the username exists and has an
     * email on file. Always completes without signaling whether the username was found, to avoid
     * account enumeration.
     *
     * @param userName the account to send a reset code to
     */
    void forgotPassword(String userName);

    /**
     * Validates a presented OTP against the user's live (unused) code.
     *
     * @param userName the account the code was issued to
     * @param otp      the code the user entered
     */
    void validateOtp(String userName, String otp);

    /**
     * Sets a new password for a user whose OTP has already been validated.
     *
     * @param userName               the account to reset
     * @param newPassword            the new password, checked against the strength policy
     * @param terminateOtherSessions when true, revokes every refresh token the user currently holds
     */
    void resetPassword(String userName, String newPassword, boolean terminateOtherSessions);
}
