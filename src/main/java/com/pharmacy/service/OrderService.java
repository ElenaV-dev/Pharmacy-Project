package com.pharmacy.service;

import com.pharmacy.dto.order_dto.OrderAdminResponseDto;
import com.pharmacy.dto.order_dto.OrderClientResponseDto;
import com.pharmacy.dto.order_dto.OrderStatusUpdateDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.OrderMapper;
import com.pharmacy.model.*;
import com.pharmacy.repository.CartRepository;
import com.pharmacy.repository.OrderRepository;
import com.pharmacy.repository.ProductRepository;
import com.pharmacy.repository.UserRepository;
import com.pharmacy.validator.CartItemValidator;
import com.pharmacy.validator.OrderValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class OrderService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemValidator cartItemValidator;
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OrderValidator orderValidator;

    public OrderService(UserRepository userRepository, CartRepository cartRepository, ProductRepository productRepository, CartItemValidator cartItemValidator, OrderMapper orderMapper, OrderRepository orderRepository, OrderValidator orderValidator) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.cartItemValidator = cartItemValidator;
        this.orderMapper = orderMapper;
        this.orderRepository = orderRepository;
        this.orderValidator = orderValidator;
    }

    @Transactional
    public OrderClientResponseDto createOrder() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found with user id " + user.getId()));

        List<CartItem> cartItems = cart.getCartItems();
        orderValidator.validateCartNotEmpty(cartItems);

        Order order = Order.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .orderDate(LocalDateTime.now())
                .totalPrice(countTotalPrice(cartItems))
                .status(OrderStatus.PROCESSING)
                .build();

        for (CartItem items : cartItems) {
            Product product = productRepository.findById(items.getProduct().getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Product not found: " + items.getProduct().getName()));

            int quantity = items.getQuantity();
            cartItemValidator.validateQuantity(quantity, product);
            product.setStock(product.getStock() - quantity);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getOrderItems().add(orderItem);
        }

        orderRepository.save(order);
        cart.getCartItems().clear();

        log.info("Order created: order_number={}, user_id={}, total_price={}",
                order.getOrderNumber(),
                user.getId(),
                order.getTotalPrice());

        return orderMapper.toOrderClientResponseDto(order);
    }

    public List<OrderClientResponseDto> getCurrentUserOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));


        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orderMapper.toOrderClientResponseDtoList(orders);
    }

    public List<OrderAdminResponseDto> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return orderMapper.toOrderAdminResponseDtoList(orders);
    }

    @Transactional
    public OrderAdminResponseDto updateStatus(Long id, OrderStatusUpdateDto dto) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        OrderStatus currentStatus = order.getStatus();
        OrderStatus newStatus = dto.getStatus();
        orderValidator.validateOrderTransition(currentStatus, newStatus);

        if (currentStatus == OrderStatus.PROCESSING && newStatus == OrderStatus.CANCELED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Product not found: " + item.getProduct().getName()));

                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
        }
        order.setStatus(newStatus);
        orderRepository.save(order);

        return orderMapper.toOrderAdminResponseDto(order);
    }

    private BigDecimal countTotalPrice(List<CartItem> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : items) {
            BigDecimal productPrice = item.getProduct().getPrice();
            BigDecimal itemTotal = productPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }

        return totalPrice;
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);

        String orderNumber;

        do {
            int randomNumber = ThreadLocalRandom.current()
                    .nextInt(1, 1_000_000);

            orderNumber = String.format("ORD-%s-%06d", date, randomNumber);
        } while (orderRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }
}
