package com.pharmacy.validator;

import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderValidator {

    public void validateCartNotEmpty(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new ValidationException("cart", "Cannot create order from empty cart");
        }
    }
}
