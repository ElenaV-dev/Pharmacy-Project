package com.pharmacy.mapper;

import com.pharmacy.dto.product_dto.ProductResponseDto;
import com.pharmacy.dto.product_dto.ProductSearchResponseDto;
import com.pharmacy.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {

    public ProductSearchResponseDto toProductSearchResponseDto(Product product) {
        return new ProductSearchResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock() > 0
        );
    }

    public List<ProductSearchResponseDto> toSearchResponseDtoList(List<Product> products) {
        return products.stream()
                .map(this::toProductSearchResponseDto)
                .toList();
    }

    public ProductResponseDto toResponseDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getInstructions()
        );
    }
}
