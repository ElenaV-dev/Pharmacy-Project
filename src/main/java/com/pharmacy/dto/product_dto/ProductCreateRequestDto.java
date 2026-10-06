package com.pharmacy.dto.product_dto;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.model.ProductCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCreateRequestDto {

    @NotBlank(message = ErrorMessageConstants.PRODUCT_NAME_REQUIRED)
    @Size(max = 100, message = ErrorMessageConstants.PRODUCT_NAME_SIZE)
    private String name;

    @NotNull(message = ErrorMessageConstants.PRODUCT_PRICE_REQUIRED)
    @DecimalMin(value = "0.01", message = ErrorMessageConstants.PRODUCT_PRICE_MIN)
    private BigDecimal price;

    @NotNull(message = ErrorMessageConstants.PRODUCT_STOCK_REQUIRED)
    @PositiveOrZero(message = ErrorMessageConstants.PRODUCT_STOCK_POSITIVE)
    private Integer stock;

    @NotNull(message = ErrorMessageConstants.PRODUCT_CATEGORY_REQUIRED)
    private ProductCategory category;

    @NotBlank(message = ErrorMessageConstants.PRODUCT_INSTRUCTION_REQUIRED)
    private String instruction;
}
