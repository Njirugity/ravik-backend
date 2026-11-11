package net.ravik_cms.ravik_backend.common.exception;

/**
 * Thrown when a candidate password fails the project's strength policy
 * ({@code net.ravik_cms.ravik_backend.common.password.PasswordPolicy}).
 */
public class WeakPasswordException extends RuntimeException {
    public WeakPasswordException(String message) {
        super(message);
    }
}
