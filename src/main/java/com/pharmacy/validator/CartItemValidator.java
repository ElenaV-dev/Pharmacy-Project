package com.pharmacy.validator;

import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.Product;
import com.pharmacy.model.User;
import com.pharmacy.repository.CartItemRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class CartItemValidator {

    private final CartItemRepository cartItemRepository;

    public CartItemValidator(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public void validateQuantity(Integer quantity, Product product) {

        if (quantity > product.getStock()) {
            throw new ValidationException("quantity", "Only " + product.getStock() + " items are available");
        }
    }

    public void validateOwnership(CartItem cartItem, User user) {
        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("Cart item does not belong to current user");
        }
    }
}
