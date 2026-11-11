package net.ravik_cms.ravik_backend.common.exception;

/**
 * Thrown by {@code change-password} when the presented current password doesn't match. Deliberately
 * not {@link InvalidCredentialsException} (401) — this is a validation failure on an already-
 * authenticated request, not an authentication failure, and 401 here would be indistinguishable
 * from an expired/invalid access token to a client-side 401 interceptor.
 */
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
