package com.pharmacy.dto.cart_dto;

import com.pharmacy.dto.cart_item_dto.CartItemRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDto {

    @NotEmpty
    @Valid
    private List<CartItemRequestDto> items;
}
