package com.pharmacy.service;

import com.pharmacy.dto.cart_dto.CartRequestDto;
import com.pharmacy.dto.cart_dto.CartResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.CartItemMapper;
import com.pharmacy.mapper.CartMapper;
import com.pharmacy.model.Cart;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.Product;
import com.pharmacy.model.User;
import com.pharmacy.repository.CartItemRepository;
import com.pharmacy.repository.CartRepository;
import com.pharmacy.repository.ProductRepository;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.validator.CartValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CartService {

    private final CartValidator cartValidator;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;

    public CartService(CartValidator cartValidator, UserRepository userRepository, CartRepository cartRepository, ProductRepository productRepository, CartItemRepository cartItemRepository, CartMapper cartMapper) {
        this.cartValidator = cartValidator;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartMapper = cartMapper;
    }

    @Transactional
    public CartResponseDto merge(CartRequestDto dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    log.debug("Creating new cart for user_id={}", user.getId());

                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(newCart);
                });

        for (CartItemRequestDto itemDto : dto.getItems()) {

            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product not found: " + itemDto.getProductId()));

            Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(
                    cart.getId(), product.getId());

            if (existingItem.isPresent()) {
                CartItem cartItem = existingItem.get();
                int newQuantity = cartItem.getQuantity() + itemDto.getQuantity();
                cartValidator.validateQuantity(newQuantity, product);
                cartItem.setQuantity(newQuantity);
            } else {
                cartValidator.validateQuantity(itemDto.getQuantity(), product);

                CartItem cartItem = CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(itemDto.getQuantity())
                        .build();

                cart.getCartItems().add(cartItem);
            }
        }
        cartRepository.save(cart);

        log.info("Cart merged: user_id={}, cart_id={}, items_count={}",
                user.getId(),
                cart.getId(),
                cart.getCartItems().size());

        return cartMapper.toCartResponseDto(cart);
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = getUserByEmail(email);
        Optional<Cart> cart = cartRepository.findByUserId(user.getId());

        if (cart.isEmpty()) {
            log.info("Cart is empty: user_id={}", user.getId());
            return CartResponseDto.builder()
                    .items(List.of())
                    .build();
        }

        log.info("Cart retrieved: user_id={}, cart_id={}, items_count={}",
                user.getId(),
                cart.get().getId(),
                cart.get().getCartItems().size());

        return cartMapper.toCartResponseDto(cart.get());
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
