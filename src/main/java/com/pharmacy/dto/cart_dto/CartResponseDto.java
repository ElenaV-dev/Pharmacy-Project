package com.pharmacy.dto.cart_dto;

import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CartResponseDto {

    private List<CartItemResponseDto> items;
}
