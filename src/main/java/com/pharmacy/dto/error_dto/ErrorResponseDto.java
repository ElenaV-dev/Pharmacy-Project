package com.pharmacy.dto.error_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponseDto {

    private final String message;
    private final Map<String, String> fieldErrors;
}
