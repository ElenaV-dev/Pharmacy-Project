package com.pharmacy.mapper;

import com.pharmacy.dto.order_dto.OrderClientResponseDto;
import com.pharmacy.model.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderClientResponseDto toOrderClientResponseDto(Order order) {
        return OrderClientResponseDto.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderDate(order.getOrderDate())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .build();
    }

    public List<OrderClientResponseDto> toOrderClientResponseDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toOrderClientResponseDto)
                .toList();
    }
}
