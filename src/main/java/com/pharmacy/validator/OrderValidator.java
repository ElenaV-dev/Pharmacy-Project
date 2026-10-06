package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderValidator {

    public void validateCartNotEmpty(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new ValidationException(
                    ValidationFieldConstants.CART,
                    ErrorMessageConstants.CART_EMPTY);
        }
    }

    public void validateOrderTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus != OrderStatus.PROCESSING) {
            throw new IllegalStateException("Order status cannot be changed");
        }

        if (newStatus != OrderStatus.COMPLETED && newStatus != OrderStatus.CANCELED) {
            throw new IllegalArgumentException("Invalid order status");
        }
    }
}
