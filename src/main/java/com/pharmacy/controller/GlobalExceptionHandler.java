package com.pharmacy.controller;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.dto.error_dto.ErrorResponseDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation failed: {}", errors);

        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ErrorMessageConstants.VALIDATION_FAILED)
                .fieldErrors(errors)
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(ValidationException ex) {

        log.warn("Business validation failed: field={}, message={}",
                ex.getField(),
                ex.getMessage()
        );

        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .fieldErrors(Map.of(ex.getField(), ex.getMessage()))
                .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException ex) {

        log.warn("Entity not found: {}", ex.getMessage());

        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ex.getMessage())
                .fieldErrors(Map.of())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleAuthenticationException(AuthenticationException ex) {

        log.warn("Authentication failed: {}", ex.getMessage());

        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ErrorMessageConstants.INVALID_CREDENTIALS)
                .fieldErrors(Map.of())
                .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException ex) {

        log.warn("Access denied: {}", ex.getMessage());

        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ErrorMessageConstants.ACCESS_DENIED)
                .fieldErrors(Map.of())
                .build();

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpectedException(Exception ex) {

        log.error("Unexpected error", ex);

        ErrorResponseDto response = ErrorResponseDto.builder()
                .message(ErrorMessageConstants.INTERNAL_SERVER_ERROR)
                .fieldErrors(Map.of())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
