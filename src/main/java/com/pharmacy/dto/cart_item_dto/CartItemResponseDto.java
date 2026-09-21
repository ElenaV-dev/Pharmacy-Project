package com.pharmacy.dto.cart_item_dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class CartItemResponseDto {

    private Long id;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
}
