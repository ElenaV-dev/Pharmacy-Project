package com.pharmacy.mapper;

import com.pharmacy.dto.product_dto.*;
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
                product.getStock() > 0,
                product.getStock(),
                product.getInstructions()
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

    public Product productCreateDtoToEntity(ProductCreateRequestDto dto) {
        Product product = new Product();

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setInstructions(dto.getInstruction());

        return product;
    }

    public void updateEntityFromDto(Product product, ProductUpdateDto dto) {
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCategory(dto.getCategory());
        product.setInstructions(dto.getInstruction());
    }

    public ProductCreateResponseDto toProductCreateResponseDto(Product product) {
        return new ProductCreateResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getInstructions()
        );
    }
}
