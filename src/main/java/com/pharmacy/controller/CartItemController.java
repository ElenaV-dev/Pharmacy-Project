package com.pharmacy.controller;

import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import com.pharmacy.dto.cart_item_dto.CartItemResponseDto;
import com.pharmacy.dto.cart_item_dto.CartItemUpdateDto;
import com.pharmacy.service.CartItemService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiEndpointConstants.CART_ITEM)
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    @PostMapping
    public CartItemResponseDto createCartItem(@Valid @RequestBody CartItemRequestDto dto) {
        return cartItemService.createCartItem(dto);
    }

    @PutMapping(ApiEndpointConstants.CART_ITEM_BY_ID)
    public CartItemResponseDto updateQuantity(
            @PathVariable("id") Long cartItemId,
            @Valid @RequestBody CartItemUpdateDto dto) {

        return cartItemService.updateQuantity(cartItemId, dto);
    }

    @DeleteMapping(ApiEndpointConstants.CART_ITEM_BY_ID)
    public void deleteById(@PathVariable("id") Long cartItemId) {
        cartItemService.deleteById(cartItemId);
    }
}
