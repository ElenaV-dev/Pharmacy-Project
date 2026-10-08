package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.Product;
import com.pharmacy.model.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class CartItemValidator {

    public void validateQuantity(Integer quantity, Product product) {
        if (quantity > product.getStock()) {
            throw new ValidationException(
                    ValidationFieldConstants.QUANTITY,
                    String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()));
        }
    }

    public void validateOwnership(CartItem cartItem, User user) {
        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(ErrorMessageConstants.CART_ITEM_ACCESS_DENIED);
        }
    }
}
