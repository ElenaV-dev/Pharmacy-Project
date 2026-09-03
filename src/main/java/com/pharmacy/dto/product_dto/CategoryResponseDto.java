package com.pharmacy.dto.product_dto;

import com.pharmacy.model.ProductCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private ProductCategory category;
    private String name;
}
