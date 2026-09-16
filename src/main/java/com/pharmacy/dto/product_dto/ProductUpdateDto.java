package com.pharmacy.dto.product_dto;

import com.pharmacy.model.ProductCategory;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateDto {

    @NotBlank(message = "Название товара обязательно для заполнения")
    @Size(max = 100, message = "Название товара не должно превышать 100 символов")
    private String name;

    @NotNull(message = "Цена обязательна для заполнения")
    @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @NotNull(message = "Количество товара обязательно для заполнения")
    @PositiveOrZero(message = "Количество товара не может быть отрицательным")
    private Integer stock;

    @NotNull(message = "Категория товара обязательна для заполнения")
    private ProductCategory category;

    @NotBlank(message = "Инструкция обязательна для заполнения")
    private String instruction;
}
