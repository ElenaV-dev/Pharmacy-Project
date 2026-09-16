package com.pharmacy.dto.product_dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class ProductSearchResponseDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private boolean inStock;
    private Integer stock;
    private String instruction;
}
