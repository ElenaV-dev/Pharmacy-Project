package com.pharmacy.dto.user_dto;

import com.pharmacy.model.UserRole;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {

    private String token;
    private UserRole role;
}
