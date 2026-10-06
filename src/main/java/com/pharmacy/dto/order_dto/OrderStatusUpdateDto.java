package com.pharmacy.dto.order_dto;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateDto {

    @NotNull(message = ErrorMessageConstants.ORDER_STATUS_REQUIRED)
    private OrderStatus status;
}
