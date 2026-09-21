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

    @NotNull(message = "Id продукта обязательно для заполнения")
    private Long productId;

    @NotNull(message = "Количество обязательно для заполнения")
    @Positive(message = "Количество должно быть больше 0")
    private Integer quantity;
}
