package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.dto.product_dto.ProductCreateRequestDto;
import com.pharmacy.dto.product_dto.ProductUpdateDto;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductValidatorTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductValidator validator;

    @Test
    void validateCreateDto_shouldNotThrowException_whenProductDoesNotExist () {
        ProductCreateRequestDto dto = ProductCreateRequestDto.builder()
                .name("Парацетамол")
                .build();

        when(productRepository.existsByName(dto.getName())).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateCreateDto(dto));
    }

    @Test
    void validateCreateDto_shouldThrowException_whenProductExists() {
        ProductCreateRequestDto dto = ProductCreateRequestDto.builder()
                .name("Парацетамол")
                .build();

        when(productRepository.existsByName(dto.getName())).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateCreateDto(dto));

        assertEquals(ValidationFieldConstants.NAME, exception.getField());
        assertEquals(ErrorMessageConstants.PRODUCT_EXISTS, exception.getMessage());
    }

    @Test
    void validateUpdateDto_shouldNotThrowException_whenProductDoesNotExist() {
        Long id = 1L;

        ProductUpdateDto dto = ProductUpdateDto.builder()
                .name("Парацетамол")
                .build();

        when(productRepository.existsByNameAndIdNot(dto.getName(), id)).thenReturn(false);

        assertDoesNotThrow(() -> validator.validateUpdateDto(id, dto));
    }

    @Test
    void validateUpdateDto_shouldThrowException_whenProductExists() {
        Long id = 1L;

        ProductUpdateDto dto = ProductUpdateDto.builder()
                .name("Парацетамол")
                .build();

        when(productRepository.existsByNameAndIdNot(dto.getName(), id)).thenReturn(true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateUpdateDto(id, dto));

        assertEquals(ValidationFieldConstants.NAME, exception.getField());
        assertEquals(ErrorMessageConstants.PRODUCT_EXISTS, exception.getMessage());
    }
}
