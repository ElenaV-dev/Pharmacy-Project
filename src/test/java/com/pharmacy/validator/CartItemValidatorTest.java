package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.Cart;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.Product;
import com.pharmacy.model.User;
import org.junit.jupiter.api.Test;

import org.springframework.security.access.AccessDeniedException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartItemValidatorTest {

    private final CartItemValidator validator = new CartItemValidator();

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

    @Test
    void validateOwnership_shouldNotThrowException_whenCartItemBelongsUser() {
        User user = User.builder()
                .id(1L)
                .build();

        Cart cart = Cart.builder()
                .user(user)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .build();

        assertDoesNotThrow(() -> validator.validateOwnership(cartItem, user));
    }

    @Test
    void validateOwnership_shouldThrowException_whenCartItemDoesNotBelongUser() {
        User user1 = User.builder()
                .id(1L)
                .build();

        User user2 = User.builder()
                .id(2L)
                .build();

        Cart cart = Cart.builder()
                .user(user1)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .build();

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> validator.validateOwnership(cartItem, user2));

        assertEquals(ErrorMessageConstants.CART_ITEM_ACCESS_DENIED, exception.getMessage());
    }
}
