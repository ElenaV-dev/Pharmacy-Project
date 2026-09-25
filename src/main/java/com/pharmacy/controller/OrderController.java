package com.pharmacy.controller;

import com.pharmacy.dto.order_dto.OrderClientResponseDto;
import com.pharmacy.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderClientResponseDto createOrder() {
        return orderService.createOrder();
    }

    @GetMapping
    public List<OrderClientResponseDto> getOrders() {
        return orderService.getCurrentUserOrders();
    }
}
