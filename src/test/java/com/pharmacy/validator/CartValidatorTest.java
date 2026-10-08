package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CartValidatorTest {

    private final CartValidator validator = new CartValidator();

    @Test
    void validateQuantity_shouldNotThrowException_whenQuantityIsLessStock() {
        int quantity = 5;

        Product product = Product.builder()
                .stock(50)
                .build();

        assertDoesNotThrow(() -> validator.validateQuantity(quantity, product));
    }

    @Test
    void validateQuantity_shouldThrowException_whenQuantityIsMoreStock() {
        int quantity = 50;

        Product product = Product.builder()
                .stock(5)
                .build();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateQuantity(quantity, product));

        assertEquals(ValidationFieldConstants.QUANTITY, exception.getField());
        assertEquals(
                String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()),
                exception.getMessage());
    }

    @Test
    void validateQuantity_shouldNotThrowException_whenQuantityIsEqualsStock() {
        int quantity = 50;

        Product product = Product.builder()
                .stock(50)
                .build();

        assertDoesNotThrow(() -> validator.validateQuantity(quantity, product));
    }
}
