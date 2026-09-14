package com.pharmacy.dto.cart_item_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDto {

    @NotNull
    private Long productId;

    @NotNull
    @Positive
    private Integer quantity;
}
