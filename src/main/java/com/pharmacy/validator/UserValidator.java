package com.pharmacy.validator;

import com.pharmacy.dto.user_dto.UserCreateDto;
import com.pharmacy.dto.user_dto.UserRegisterDto;
import com.pharmacy.dto.user_dto.UserUpdateDto;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validateRegisterDto(UserRegisterDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("email", "Пользователь с таким email уже существует");
        }
    }

    public void validateCreateDto(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException("email", "Пользователь с таким email уже существует");
        }
    }

    public void validateUpdateDto(UserUpdateDto dto, Long id) {
        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ValidationException("email", "Пользователь с таким email уже существует");
        }
    }
}
