package com.pharmacy.service;

import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemUpdateDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.CartItemMapper;
import com.pharmacy.model.Cart;
import com.pharmacy.model.CartItem;
import com.pharmacy.model.Product;
import com.pharmacy.model.User;
import com.pharmacy.repository.CartItemRepository;
import com.pharmacy.repository.CartRepository;
import com.pharmacy.repository.ProductRepository;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.validator.CartItemValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final CartItemValidator cartItemValidator;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartItemService(CartItemMapper cartItemMapper, UserRepository userRepository, CartRepository cartRepository, ProductRepository productRepository, CartItemValidator cartItemValidator, CartItemRepository cartItemRepository) {
        this.cartItemMapper = cartItemMapper;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemValidator = cartItemValidator;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public CartItemResponseDto createCartItem(CartItemRequestDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = getUserByEmail(email);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + dto.getProductId()));

        Integer quantity = dto.getQuantity();
        cartItemValidator.validateQuantity(quantity, product);

        Optional<CartItem> existingCartItem = cartItemRepository.findByCartIdAndProductId(
                cart.getId(),
                product.getId());

        CartItem cartItem;

        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            cartItemValidator.validateQuantity(newQuantity, product);
            cartItem.setQuantity(newQuantity);

            log.info("Cart item quantity increased: cart_id={}, product_id={}, quantity={}",
                    cart.getId(), product.getId(), newQuantity);
        } else {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();

            log.info("Cart item created: cart_id={}, product_id={}, quantity={}",
                    cart.getId(), product.getId(), quantity);
        }
        cartItemRepository.save(cartItem);

        return cartItemMapper.toCartItemResponseDto(cartItem);
    }

    @Transactional
    public CartItemResponseDto updateQuantity(Long cartItemId, CartItemUpdateDto dto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = getUserByEmail(email);
        CartItem cartItem = getCartItemById(cartItemId);
        cartItemValidator.validateOwnership(cartItem, user);
        Product product = cartItem.getProduct();
        Integer quantity = dto.getQuantity();
        cartItemValidator.validateQuantity(quantity, product);
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        log.info("Cart item quantity updated: cart_item_id={}, product_id={}, quantity={}",
                cartItemId, product.getId(), quantity);

        return cartItemMapper.toCartItemResponseDto(cartItem);
    }

    @Transactional
    public void deleteById(Long cartItemId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = getUserByEmail(email);
        CartItem cartItem = getCartItemById(cartItemId);
        cartItemValidator.validateOwnership(cartItem, user);
        cartItemRepository.delete(cartItem);
        log.info("Cart item deleted: cart_item_id={}", cartItemId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private CartItem getCartItemById(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id " + id));
    }
}
