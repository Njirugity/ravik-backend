package net.ravik_cms.ravik_backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrors> resourceNotFoundException(ResourceNotFoundException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDate.now());
        return ResponseEntity.badRequest().body(errors);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrors> argumentNotFoundException(MethodArgumentNotValidException e){
        ApiErrors errors = new ApiErrors(e.getMessage(), HttpStatus.BAD_REQUEST.value(), LocalDate.now());
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
}
