package com.pharmacy.controller;

import com.pharmacy.dto.error_dto.ErrorResponseDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                .message("Validation failed")
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
}
