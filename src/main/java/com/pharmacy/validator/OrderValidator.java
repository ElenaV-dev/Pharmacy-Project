package com.pharmacy.validator;

import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.OrderStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderValidator {

    public void validateCartNotEmpty(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new ValidationException("cart", "Cannot create order from empty cart");
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
