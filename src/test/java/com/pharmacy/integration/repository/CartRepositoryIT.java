package com.pharmacy.integration.repository;

import com.pharmacy.model.Cart;
import com.pharmacy.model.User;
import com.pharmacy.model.UserRole;
import com.pharmacy.repository.CartRepository;
import com.pharmacy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CartRepositoryIT {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserId_shouldReturnCart_whenCartExists() {

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

        cartRepository.save(cart);

        Optional<Cart> result = cartRepository.findByUserId(user.getId());

        assertTrue(result.isPresent());
        assertEquals(cart.getId(), result.get().getId());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUserId_shouldReturnEmptyOptional_whenCartDoesNotExist() {
        Optional<Cart> result = cartRepository.findByUserId(1L);

        assertTrue(result.isEmpty());
    }
}
