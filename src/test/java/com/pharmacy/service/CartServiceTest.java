package com.pharmacy.service;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.dto.cart_dto.CartRequestDto;
import com.pharmacy.dto.cart_dto.CartResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.exception.ValidationException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    private static final String TEST_EMAIL = "test@mail.com";

    @Mock
    private CartValidator cartValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

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
    void merge_shouldSaveCart_whenCartItemDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(1L)
                .stock(50)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.empty());

        CartItemRequestDto itemDto = CartItemRequestDto.builder()
                .productId(product.getId())
                .quantity(2)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(itemDto))
                .build();

        CartResponseDto responseDto = CartResponseDto.builder().build();

        when(cartMapper.toCartResponseDto(cart)).thenReturn(responseDto);

        CartResponseDto result = cartService.merge(requestDto);

        assertEquals(responseDto, result);

        verify(cartValidator).validateQuantity(itemDto.getQuantity(), product);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();

        assertEquals(user, savedCart.getUser());

        verify(cartMapper).toCartResponseDto(savedCart);
    }

    @Test
    void merge_shouldSaveCart_whenCartItemExists() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(1L)
                .stock(50)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        cart.getCartItems().add(cartItem);

        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(cartItem));

        CartItemRequestDto itemDto = CartItemRequestDto.builder()
                .productId(product.getId())
                .quantity(3)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(itemDto))
                .build();

        CartResponseDto responseDto = CartResponseDto.builder().build();

        when(cartMapper.toCartResponseDto(cart)).thenReturn(responseDto);

        CartResponseDto result = cartService.merge(requestDto);

        assertEquals(responseDto, result);
        assertEquals(5, cartItem.getQuantity());

        verify(cartValidator).validateQuantity(5, product);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();

        assertEquals(user, savedCart.getUser());

        verify(cartMapper).toCartResponseDto(savedCart);
    }

    @Test
    void merge_shouldCreateNewCart_whenCartDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        Product product = Product.builder()
                .id(1L)
                .stock(50)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L, product.getId()))
                .thenReturn(Optional.empty());

        CartItemRequestDto itemDto = CartItemRequestDto.builder()
                .productId(product.getId())
                .quantity(3)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(itemDto))
                .build();

        CartResponseDto responseDto = CartResponseDto.builder().build();

        when(cartMapper.toCartResponseDto(any(Cart.class))).thenReturn(responseDto);

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart cartToSave = invocation.getArgument(0);
            cartToSave.setId(1L);
            return cartToSave;
        });

        CartResponseDto result = cartService.merge(requestDto);

        assertEquals(responseDto, result);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);

        verify(cartRepository, times(2)).save(cartCaptor.capture());

        Cart savedCart = cartCaptor.getValue();

        assertEquals(user, savedCart.getUser());

        verify(cartValidator).validateQuantity(itemDto.getQuantity(), product);

        CartItem savedCartItem = savedCart.getCartItems().get(0);

        assertEquals(savedCart, savedCartItem.getCart());
        assertEquals(product, savedCartItem.getProduct());
        assertEquals(itemDto.getQuantity(), savedCartItem.getQuantity());

        verify(cartMapper).toCartResponseDto(savedCart);
    }

    @Test
    void merge_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        CartRequestDto dto = CartRequestDto.builder().build();

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> cartService.merge(dto));

        assertEquals(
                String.format(ErrorMessageConstants.USER_NOT_FOUND_BY_EMAIL, "test@mail.com"),
                exception.getMessage());

        verify(userRepository).findByEmail("test@mail.com");
        verify(cartRepository, never()).findByUserId(anyLong());
        verify(cartRepository, never()).save(any(Cart.class));
        verify(productRepository, never()).findById(anyLong());
        verify(cartItemRepository, never()).findByCartIdAndProductId(anyLong(), anyLong());
        verify(cartValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartMapper, never()).toCartResponseDto(any(Cart.class));
    }

    @Test
    void merge_shouldThrowException_whenProductNotFound() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        CartItemRequestDto itemDto = CartItemRequestDto.builder()
                .productId(1L)
                .quantity(3)
                .build();

        when(productRepository.findById(itemDto.getProductId())).thenReturn(Optional.empty());

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(itemDto))
                .build();

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> cartService.merge(requestDto));

        assertEquals(String.format(ErrorMessageConstants.PRODUCT_NOT_FOUND_BY_ID, itemDto.getProductId()),
                exception.getMessage());

        verify(productRepository).findById(itemDto.getProductId());
        verify(cartItemRepository, never()).findByCartIdAndProductId(anyLong(), anyLong());
        verify(cartValidator, never()).validateQuantity(anyInt(), any(Product.class));
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).toCartResponseDto(any(Cart.class));
    }

    @Test
    void merge_shouldThrowException_whenCartItemDoesNotExistAndStockIsNotEnough() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(1L)
                .stock(1)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.empty());

        CartItemRequestDto itemDto = CartItemRequestDto.builder()
                .productId(product.getId())
                .quantity(2)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(itemDto))
                .build();

        doThrow(new ValidationException(
                ValidationFieldConstants.QUANTITY,
                String.format(
                        ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED,
                        product.getStock())))
                .when(cartValidator)
                .validateQuantity(itemDto.getQuantity(), product);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> cartService.merge(requestDto));

        assertEquals(String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()),
                exception.getMessage());

        verify(cartValidator).validateQuantity(itemDto.getQuantity(), product);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).toCartResponseDto(any(Cart.class));
    }

    @Test
    void merge_shouldThrowException_whenCartItemExistsAndStockIsNotEnough() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        Product product = Product.builder()
                .id(1L)
                .stock(1)
                .build();

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(2)
                .build();

        when(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()))
                .thenReturn(Optional.of(cartItem));

        CartItemRequestDto itemDto = CartItemRequestDto.builder()
                .productId(product.getId())
                .quantity(2)
                .build();

        CartRequestDto requestDto = CartRequestDto.builder()
                .items(List.of(itemDto))
                .build();

        doThrow(new ValidationException(
                ValidationFieldConstants.QUANTITY,
                String.format(
                        ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED,
                        product.getStock())))
                .when(cartValidator)
                .validateQuantity(itemDto.getQuantity() + cartItem.getQuantity(), product);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> cartService.merge(requestDto));

        assertEquals(String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()),
                exception.getMessage());

        verify(cartValidator).validateQuantity(itemDto.getQuantity() + cartItem.getQuantity(), product);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).toCartResponseDto(any(Cart.class));
    }

    @Test
    void getCart_shouldReturnCart_whenCartExists() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .cartItems(new ArrayList<>())
                .build();

        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.of(cart));

        CartResponseDto responseDto = CartResponseDto.builder().build();

        when(cartMapper.toCartResponseDto(cart)).thenReturn(responseDto);

        CartResponseDto result = cartService.getCart();

        assertEquals(responseDto, result);

        verify(cartRepository).findByUserId(user.getId());
        verify(cartMapper).toCartResponseDto(cart);
    }

    @Test
    void getCart_shouldReturnEmptyCart_whenCartDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .email("test@mail.com")
                .build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(user.getId())).thenReturn(Optional.empty());

        CartResponseDto result = cartService.getCart();

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertTrue(result.getItems().isEmpty());

        verify(cartRepository).findByUserId(user.getId());
        verify(cartMapper, never()).toCartResponseDto(any(Cart.class));
    }

    @Test
    void getCart_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> cartService.getCart());

        assertEquals(
                String.format(ErrorMessageConstants.USER_NOT_FOUND_BY_EMAIL, "test@mail.com"),
                exception.getMessage());

        verify(userRepository).findByEmail("test@mail.com");
        verify(cartRepository, never()).findByUserId(anyLong());
        verify(cartMapper, never()).toCartResponseDto(any(Cart.class));
    }
}
