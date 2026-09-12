package net.ravik_cms.ravik_backend.common.exception;

public class FieldRequiredException extends RuntimeException {
    public FieldRequiredException(String message) {
        super(message);
    }
}
