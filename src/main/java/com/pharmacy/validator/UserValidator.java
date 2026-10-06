package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
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
            throw new ValidationException(
                    ValidationFieldConstants.EMAIL,
                    String.format(ErrorMessageConstants.USER_EXISTS_BY_EMAIL, dto.getEmail()));
        }
    }

    public void validateCreateDto(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ValidationException(
                    ValidationFieldConstants.EMAIL,
                    String.format(ErrorMessageConstants.USER_EXISTS_BY_EMAIL, dto.getEmail()));
        }
    }

    public void validateUpdateDto(UserUpdateDto dto, Long id) {
        if (userRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new ValidationException(
                    ValidationFieldConstants.EMAIL,
                    String.format(ErrorMessageConstants.USER_EXISTS_BY_EMAIL, dto.getEmail()));
        }
    }
}
