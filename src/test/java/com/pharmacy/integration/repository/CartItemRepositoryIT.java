package com.pharmacy.integration.repository;

import com.pharmacy.model.*;
import com.pharmacy.repository.CartItemRepository;
import com.pharmacy.repository.CartRepository;
import com.pharmacy.repository.ProductRepository;
import com.pharmacy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartItemRepositoryIT {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByCartIdAndProductId_shouldReturnCartItem_whenCartItemExists() {

        User user = User.builder()
                .name("Test User")
                .email("test@mail.com")
                .password("encodedPassword")
                .role(UserRole.CLIENT)
                .build();

        user = userRepository.save(user);

        Cart cart = Cart.builder()
                .user(user)
                .build();

        cart = cartRepository.save(cart);

        Product product = Product.builder()
                .name("Парацетамол")
                .price(new BigDecimal("1500.00"))
                .stock(50)
                .category(ProductCategory.MEDICINE)
                .instructions("")
                .build();

        product = productRepository.save(product);

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cartItem = cartItemRepository.save(cartItem);

        Optional<CartItem> result = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        assertTrue(result.isPresent());
        assertEquals(cart.getId(), result.get().getCart().getId());
        assertEquals(product.getId(), result.get().getProduct().getId());
        assertEquals(cartItem.getId(), result.get().getId());
    }
}
