package com.pharmacy.controller;

import com.pharmacy.dto.cart_dto.CartRequestDto;
import com.pharmacy.dto.cart_dto.CartResponseDto;
import com.pharmacy.service.CartService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/merge")
    public CartResponseDto merge(@Valid @RequestBody CartRequestDto dto) {
        return cartService.merge(dto);
    }
}
