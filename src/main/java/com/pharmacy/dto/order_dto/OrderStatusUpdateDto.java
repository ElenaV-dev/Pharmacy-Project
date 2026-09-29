package com.pharmacy.dto.order_dto;

import com.pharmacy.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDto {

    @NotNull(message = "Статус заказа обязателен для заполнения")
    private OrderStatus status;
}
