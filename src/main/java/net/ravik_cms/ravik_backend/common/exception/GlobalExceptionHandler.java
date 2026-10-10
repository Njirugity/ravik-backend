package net.ravik_cms.ravik_backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrors> resourceNotFoundException(ResourceNotFoundException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDate.now());
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> handleValidationException(MethodArgumentNotValidException e){
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        if (message.isBlank()) {
            message = "Validation failed";
        }
        ApiErrors errors = new ApiErrors(message, HttpStatus.BAD_REQUEST.value(), LocalDate.now());
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrors> userAlreadyExistException(UserAlreadyExistsException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDate.now());
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrors> accessDeniedException(AccessDeniedException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.FORBIDDEN.value(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }
    @ExceptionHandler(CircularDependencyException.class)
    public ResponseEntity<ApiErrors> circularDependencyException(CircularDependencyException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.CONFLICT.value(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }
    @ExceptionHandler(UnsupportedPaymentCategoryException.class)
    public ResponseEntity<ApiErrors> unsupportedPaymentCategoryException(UnsupportedPaymentCategoryException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDate.now());
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(FieldRequiredException.class)
    public ResponseEntity<ApiErrors> fieldRequiredException(FieldRequiredException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDate.now());
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrors> invalidTokenException(InvalidTokenException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrors> invalidCredentialsException(InvalidCredentialsException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.UNAUTHORIZED.value(), LocalDate.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }
}
