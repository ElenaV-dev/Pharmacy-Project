package com.pharmacy.dto.user_dto;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationPatternConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisterDto {

    @NotBlank(message = ErrorMessageConstants.USER_NAME_REQUIRED)
    @Size(max = 50, message = ErrorMessageConstants.USER_NAME_SIZE)
    @Pattern(regexp = ValidationPatternConstants.USER_NAME, message = ErrorMessageConstants.USER_NAME_PATTERN)
    private String name;

    @NotBlank(message = ErrorMessageConstants.USER_EMAIL_REQUIRED)
    @Email(message = ErrorMessageConstants.USER_EMAIL_VALID)
    @Size(max = 100, message = ErrorMessageConstants.USER_EMAIL_SIZE)
    private String email;

    @NotBlank(message = ErrorMessageConstants.USER_PASSWORD_REQUIRED)
    @Size(min = 8, message = ErrorMessageConstants.USER_PASSWORD_SIZE)
    @Pattern(regexp = ValidationPatternConstants.USER_PASSWORD,
            message = ErrorMessageConstants.USER_PASSWORD_PATTERN)
    private String password;
}
