package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.OrderStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class OrderValidatorTest {

    private final OrderValidator validator = new OrderValidator();

    @Test
    void validateCartNotEmpty_shouldNotThrowException_whenCartIsNotEmpty() {
        List<CartItem> items = List.of(new CartItem());

        assertDoesNotThrow(() -> validator.validateCartNotEmpty(items));
    }

    @Test
    void validateCartNotEmpty_shouldThrowException_whenCartIsEmpty() {
        List<CartItem> items = List.of();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateCartNotEmpty(items));

        assertEquals(ValidationFieldConstants.CART, exception.getField());
        assertEquals(ErrorMessageConstants.CART_EMPTY, exception.getMessage());
    }

    @Test
    void validateOrderTransition_shouldThrowException_whenCurrentStatusIsNotProcessing() {
        OrderStatus currentStatus = OrderStatus.CANCELED;

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateOrderTransition(currentStatus, OrderStatus.COMPLETED));

        assertEquals(ValidationFieldConstants.STATUS, exception.getField());
        assertEquals(ErrorMessageConstants.ORDER_STATUS_CANNOT_BE_CHANGED, exception.getMessage());
    }

    @Test
    void validateOrderTransition_shouldNotThrowException_whenNewStatusIsCompleted() {
        OrderStatus newStatus = OrderStatus.COMPLETED;

        assertDoesNotThrow(() -> validator.validateOrderTransition(OrderStatus.PROCESSING, newStatus));
    }

    @Test
    void validateOrderTransition_shouldNotThrowException_whenNewStatusIsCanceled() {
        OrderStatus newStatus = OrderStatus.CANCELED;

        assertDoesNotThrow(() -> validator.validateOrderTransition(OrderStatus.PROCESSING, newStatus));
    }

    @Test
    void validateOrderTransition_shouldThrowException_whenNewStatusIsInvalid() {
        OrderStatus newStatus = OrderStatus.PROCESSING;

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> validator.validateOrderTransition(OrderStatus.PROCESSING, newStatus));

        assertEquals(ValidationFieldConstants.STATUS, exception.getField());
        assertEquals(ErrorMessageConstants.ORDER_STATUS_INVALID, exception.getMessage());
    }
}
