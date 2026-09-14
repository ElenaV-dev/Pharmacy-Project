package com.pharmacy.mapper;

import com.pharmacy.dto.cart_dto.CartResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.model.Cart;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    public CartMapper(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }

    public CartResponseDto toCartResponseDto(Cart cart) {

    List<CartItemResponseDto> cartItems = cartItemMapper.toCartItemResponseDtoList(cart.getCartItems());

        return CartResponseDto.builder()
                .items(cartItems)
                .build();
    }
}
