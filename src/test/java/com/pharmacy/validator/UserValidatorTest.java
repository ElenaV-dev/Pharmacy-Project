package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.dto.user_dto.UserCreateDto;
import com.pharmacy.dto.user_dto.UserRegisterDto;
import com.pharmacy.dto.user_dto.UserUpdateDto;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserValidatorTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserValidator validator;

    @Test
    void validateRegisterDto_shouldNotThrowException_whenEmailDoesNotExist() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateRegisterDto(dto));
    }

    @Test
    void validateRegisterDto_shouldThrowException_whenEmailExists() {
        UserRegisterDto dto = UserRegisterDto.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateRegisterDto(dto));

        assertEquals(ValidationFieldConstants.EMAIL, exception.getField());
        assertEquals(
                String.format(ErrorMessageConstants.USER_EXISTS_BY_EMAIL,
                        dto.getEmail()), exception.getMessage());
    }

    @Test
    void validateCreateDto_shouldNotThrowException_whenEmailDoesNotExist() {
        UserCreateDto dto = UserCreateDto.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateCreateDto(dto));
    }

    @Test
    void validateCreateDto_shouldThrowException_whenEmailExists() {
        UserCreateDto dto = UserCreateDto.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateCreateDto(dto));

        assertEquals(ValidationFieldConstants.EMAIL, exception.getField());
        assertEquals(
                String.format(ErrorMessageConstants.USER_EXISTS_BY_EMAIL, dto.getEmail()),
                exception.getMessage());
    }

    @Test
    void validateUpdateDto_shouldNotThrowException_whenEmailDoesNotExist() {
        Long id = 1L;

        UserUpdateDto dto = UserUpdateDto.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.existsByEmailAndIdNot(dto.getEmail(), id)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateUpdateDto(dto, id));
    }

    @Test
    void validateUpdateDto_shouldThrowException_whenEmailExists() {
        Long id = 1L;

        UserUpdateDto dto = UserUpdateDto.builder()
                .email("test@mail.com")
                .build();

        when(userRepository.existsByEmailAndIdNot(dto.getEmail(), id)).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateUpdateDto(dto, id));

        assertEquals(ValidationFieldConstants.EMAIL, exception.getField());
        assertEquals(
                String.format(ErrorMessageConstants.USER_EXISTS_BY_EMAIL, dto.getEmail()),
                exception.getMessage());
    }
}
