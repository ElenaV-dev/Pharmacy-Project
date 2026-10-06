package com.pharmacy.dto.cart_item_dto;

import com.pharmacy.constants.ErrorMessageConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemUpdateDto {

    @NotNull(message = ErrorMessageConstants.PRODUCT_QUANTITY_REQUIRED)
    @Positive(message = ErrorMessageConstants.PRODUCT_QUANTITY_POSITIVE)
    private Integer quantity;
}
