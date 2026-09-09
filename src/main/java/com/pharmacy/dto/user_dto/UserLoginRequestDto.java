package com.pharmacy.dto.user_dto;

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
public class UserLoginRequestDto {

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Введите корректный адрес электронной почты")
    @Size(max = 100, message = "Email не должен превышать 100 символов")
    private String email;

    @NotBlank(message = "Пароль обязателен для заполнения")
    @Size(min = 8, message = "Пароль должен состоять минимум из 8 символов")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Пароль должен содержать как минимум одну строчную букву, " +
                    "одну заглавную букву и одну цифру")
    private String password;
}
