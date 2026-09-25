package com.pharmacy.mapper;

import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.model.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartItemMapper {

    public CartItemResponseDto toCartItemResponseDto(CartItem cartItem) {
        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity(),
                cartItem.getProduct().getStock()
        );
    }

    public List<CartItemResponseDto> toCartItemResponseDtoList(List<CartItem> items) {
        return items.stream()
                .map(this::toCartItemResponseDto)
                .toList();
    }
}
