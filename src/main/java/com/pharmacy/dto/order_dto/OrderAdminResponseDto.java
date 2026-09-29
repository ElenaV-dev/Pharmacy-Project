package com.pharmacy.dto.order_dto;

import com.pharmacy.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class OrderAdminResponseDto {
    private Long id;
    private String orderNumber;
    private String name;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private OrderStatus status;
}
