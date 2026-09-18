package com.pharmacy.dto.user_dto;

import com.pharmacy.model.UserRole;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {

    @NotBlank(message = "Имя обязательно для заполнения")
    @Size(max = 50, message = "Имя не должно превышать 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$", message = "Имя должно содержать только буквы, пробелы и дефис")
    private String name;

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

    @NotNull(message = "Роль пользователя обязательна для заполнения")
    private UserRole role;
}
