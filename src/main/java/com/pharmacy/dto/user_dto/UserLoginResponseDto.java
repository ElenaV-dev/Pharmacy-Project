package com.pharmacy.dto.user_dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {

    private String token;
}
