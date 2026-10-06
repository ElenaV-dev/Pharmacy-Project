package com.pharmacy.controller;

import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.dto.order_dto.OrderAdminResponseDto;
import com.pharmacy.dto.order_dto.OrderClientResponseDto;
import com.pharmacy.dto.order_dto.OrderStatusUpdateDto;
import com.pharmacy.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpointConstants.ORDERS)
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

    @GetMapping(ApiEndpointConstants.ORDERS_ADMIN)
    public List<OrderAdminResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PutMapping(ApiEndpointConstants.ORDERS_STATUS)
    public OrderAdminResponseDto updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateDto dto) {

        return orderService.updateStatus(id, dto);
    }
}
