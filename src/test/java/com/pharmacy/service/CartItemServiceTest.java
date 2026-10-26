package com.pharmacy.service;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemUpdateDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.exception.ValidationException;
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.access.AccessDeniedException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {

    private static final String TEST_EMAIL = "test@mail.com";

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private CartItemValidator cartItemValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;

    @BeforeEach
    void setUpSecurityContext() {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(authentication.getName()).thenReturn(TEST_EMAIL);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createCartItem_shouldCreateNewCartItem_whenProductIsNotInCart() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(10L)
                .stock(50)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(5)
                .build();

        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.empty());

        CartItemResponseDto responseDto = CartItemResponseDto.builder().build();

        when(cartItemMapper.toCartItemResponseDto(any(CartItem.class))).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.createCartItem(dto);

        assertEquals(responseDto, result);

        verify(cartItemValidator).validateQuantity(dto.getQuantity(), product);

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);

        verify(cartItemRepository).save(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertEquals(cart, savedCartItem.getCart());
        assertEquals(product, savedCartItem.getProduct());
        assertEquals(dto.getQuantity(), savedCartItem.getQuantity());

        verify(cartItemMapper).toCartItemResponseDto(savedCartItem);
    }

    @Test
    void createCartItem_shouldCreateNewCartItem_whenCartDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Product product = Product.builder()
                .id(10L)
                .stock(50)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(5)
                .build();

        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.empty());

        CartItemResponseDto responseDto = CartItemResponseDto.builder().build();

        when(cartItemMapper.toCartItemResponseDto(any(CartItem.class))).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.createCartItem(dto);

        assertEquals(responseDto, result);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();

        assertEquals(user, savedCart.getUser());

        verify(cartItemValidator).validateQuantity(dto.getQuantity(), product);

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);

        verify(cartItemRepository).save(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertEquals(cart, savedCartItem.getCart());
        assertEquals(product, savedCartItem.getProduct());
        assertEquals(dto.getQuantity(), savedCartItem.getQuantity());

        verify(cartItemMapper).toCartItemResponseDto(savedCartItem);
    }

    @Test
    void createCartItem_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .quantity(3)
                .build();

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> cartItemService.createCartItem(dto));

        assertEquals(
                String.format(ErrorMessageConstants.USER_NOT_FOUND_BY_EMAIL, TEST_EMAIL),
                exception.getMessage());

        verify(cartRepository, never()).findByUserId(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(productRepository, never()).findById(anyLong());
        verify(cartItemValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartItemRepository, never()).findByCartIdAndProductId(anyLong(), anyLong());
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void createCartItem_shouldThrowException_whenProductDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(5)
                .build();

        when(productRepository.findById(dto.getProductId())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartItemService.createCartItem(dto));

        assertEquals(
                String.format(ErrorMessageConstants.PRODUCT_NOT_FOUND_BY_ID, dto.getProductId()),
                exception.getMessage());

        verify(cartItemValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartItemRepository, never()).findByCartIdAndProductId(anyLong(), anyLong());
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void createCartItem_shouldIncreaseQuantity_whenStockIsEnough() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(10L)
                .stock(50)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(5)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(cartItem));

        CartItemResponseDto responseDto = CartItemResponseDto.builder().build();

        when(cartItemMapper.toCartItemResponseDto(any(CartItem.class))).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.createCartItem(dto);

        assertEquals(responseDto, result);

        verify(cartItemValidator).validateQuantity(5, product);
        verify(cartItemValidator).validateQuantity(7, product);

        ArgumentCaptor<CartItem> cartItemCaptor =
                ArgumentCaptor.forClass(CartItem.class);

        verify(cartItemRepository).save(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertEquals(cart, savedCartItem.getCart());
        assertEquals(product, savedCartItem.getProduct());
        assertEquals(7, savedCartItem.getQuantity());

        verify(cartItemMapper).toCartItemResponseDto(savedCartItem);
    }

    @Test
    void createCartItem_shouldThrowException_whenRequestedQuantityExceedsStock() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(10L)
                .stock(5)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(7)
                .build();

        doThrow(new ValidationException(
                ValidationFieldConstants.QUANTITY,
                String.format(
                        ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED,
                        product.getStock())))
                .when(cartItemValidator)
                .validateQuantity(dto.getQuantity(), product);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> cartItemService.createCartItem(dto));

        assertEquals(
                String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()),
                exception.getMessage());

        verify(cartItemValidator).validateQuantity(dto.getQuantity(), product);
        verify(cartItemRepository, never()).findByCartIdAndProductId(anyLong(), anyLong());
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void createCartItem_shouldThrowException_whenTotalQuantityExceedsStock() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(10L)
                .stock(5)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItemRequestDto dto = CartItemRequestDto.builder()
                .productId(10L)
                .quantity(2)
                .build();

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(10)
                .build();

        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(cartItem));

        doNothing()
                .when(cartItemValidator)
                .validateQuantity(dto.getQuantity(), product);

        doThrow(new ValidationException(
                ValidationFieldConstants.QUANTITY,
                String.format(
                        ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED,
                        product.getStock())))
                .when(cartItemValidator)
                .validateQuantity(dto.getQuantity() + cartItem.getQuantity(), product);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> cartItemService.createCartItem(dto));

        assertEquals(String.format(
                        ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()),
                exception.getMessage());

        verify(cartItemValidator).validateQuantity(dto.getQuantity(), product);
        verify(cartItemRepository).findByCartIdAndProductId(cart.getId(), product.getId());
        verify(cartItemValidator).validateQuantity(dto.getQuantity() + cartItem.getQuantity(), product);
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void updateQuantity_shouldUpdateQuantity_whenStockIsEnough() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Product product = Product.builder()
                .id(1L)
                .stock(50)
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItemUpdateDto dto = CartItemUpdateDto.builder()
                .quantity(2)
                .build();

        CartItemResponseDto responseDto = CartItemResponseDto.builder().build();

        when(cartItemMapper.toCartItemResponseDto(any(CartItem.class))).thenReturn(responseDto);

        CartItemResponseDto result = cartItemService.updateQuantity(1L, dto);

        assertEquals(responseDto, result);

        verify(cartItemValidator).validateOwnership(cartItem, user);
        verify(cartItemValidator).validateQuantity(dto.getQuantity(), product);

        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);

        verify(cartItemRepository).save(cartItemCaptor.capture());

        CartItem savedCartItem = cartItemCaptor.getValue();

        assertEquals(dto.getQuantity(), savedCartItem.getQuantity());

        verify(cartItemMapper).toCartItemResponseDto(savedCartItem);
    }

    @Test
    void updateQuantity_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .build();

        CartItemUpdateDto dto = CartItemUpdateDto.builder()
                .quantity(5)
                .build();

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> cartItemService.updateQuantity(cartItem.getId(), dto));

        assertEquals(
                String.format(ErrorMessageConstants.USER_NOT_FOUND_BY_EMAIL, TEST_EMAIL),
                exception.getMessage());

        verify(cartItemRepository, never()).findById(anyLong());
        verify(cartItemValidator, never()).validateOwnership(any(CartItem.class), any(User.class));
        verify(cartItemValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void updateQuantity_shouldThrowException_whenCartItemNotFound() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));
        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        CartItemUpdateDto dto = CartItemUpdateDto.builder()
                .quantity(5)
                .build();

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartItemService.updateQuantity(1L, dto));

        assertEquals(
                String.format(ErrorMessageConstants.CART_ITEM_NOT_FOUND_BY_ID, 1L),
                exception.getMessage());

        verify(cartItemRepository).findById(1L);
        verify(cartItemValidator, never()).validateOwnership(any(CartItem.class), any(User.class));
        verify(cartItemValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void updateQuantity_shouldThrowException_whenCartItemDoesNotBelongUser() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItemUpdateDto dto = CartItemUpdateDto.builder()
                .quantity(5)
                .build();

        doThrow(new AccessDeniedException(ErrorMessageConstants.CART_ITEM_ACCESS_DENIED))
                .when(cartItemValidator).validateOwnership(cartItem, user);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> cartItemService.updateQuantity(cartItem.getId(), dto));

        assertEquals(ErrorMessageConstants.CART_ITEM_ACCESS_DENIED, exception.getMessage());

        verify(cartItemValidator).validateOwnership(cartItem, user);
        verify(cartItemValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void updateQuantity_shouldThrowException_whenStockIsNotEnough() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        Product product = Product.builder()
                .id(1L)
                .stock(5)
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .build();

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItemUpdateDto dto = CartItemUpdateDto.builder()
                .quantity(20)
                .build();

        doThrow(new ValidationException(
                ValidationFieldConstants.QUANTITY,
                String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock())))
                .when(cartItemValidator).validateQuantity(dto.getQuantity(), product);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> cartItemService.updateQuantity(cartItem.getId(), dto));

        assertEquals(ValidationFieldConstants.QUANTITY, exception.getField());
        assertEquals(
                String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()),
                exception.getMessage());

        verify(cartItemValidator).validateOwnership(cartItem, user);
        verify(cartItemValidator).validateQuantity(dto.getQuantity(), product);
        verify(cartItemRepository, never()).save(any(CartItem.class));
        verify(cartItemMapper, never()).toCartItemResponseDto(any(CartItem.class));
    }

    @Test
    void deleteById_shouldDeleteCartItem_whenCartItemExistsAndBelongsUser() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .build();

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        cartItemService.deleteById(cartItem.getId());

        verify(cartItemValidator).validateOwnership(cartItem, user);
        verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void deleteById_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> cartItemService.deleteById(1L));

        assertEquals(
                String.format(ErrorMessageConstants.USER_NOT_FOUND_BY_EMAIL, TEST_EMAIL),
                exception.getMessage());

        verify(cartItemRepository, never()).findById(anyLong());
        verify(cartItemValidator, never()).validateOwnership(any(CartItem.class), any(User.class));
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void deleteById_shouldThrowException_whenCartItemNotFound() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartItemService.deleteById(1L));

        assertEquals(
                String.format(ErrorMessageConstants.CART_ITEM_NOT_FOUND_BY_ID, 1L),
                exception.getMessage());

        verify(cartItemRepository).findById(1L);
        verify(cartItemValidator, never()).validateOwnership(any(CartItem.class), any(User.class));
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    @Test
    void deleteById_shouldThrowException_whenCartItemDoesNotBelongUser() {
        User user = User.builder()
                .id(1L)
                .email(TEST_EMAIL)
                .build();

        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .build();

        when(cartItemRepository.findById(cartItem.getId())).thenReturn(Optional.of(cartItem));

        doThrow(new AccessDeniedException(ErrorMessageConstants.CART_ITEM_ACCESS_DENIED))
                .when(cartItemValidator).validateOwnership(cartItem, user);

        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> cartItemService.deleteById(cartItem.getId()));

        assertEquals(ErrorMessageConstants.CART_ITEM_ACCESS_DENIED, exception.getMessage());

        verify(cartItemRepository).findById(1L);
        verify(cartItemValidator).validateOwnership(cartItem, user);
        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }
}
