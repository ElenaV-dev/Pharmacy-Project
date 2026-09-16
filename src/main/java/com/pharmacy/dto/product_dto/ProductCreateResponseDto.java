package com.pharmacy.dto.product_dto;

import com.pharmacy.model.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class ProductCreateResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock;
    private ProductCategory category;
    private String instruction;
}
