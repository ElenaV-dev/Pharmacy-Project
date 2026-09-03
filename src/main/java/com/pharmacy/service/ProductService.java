package com.pharmacy.service;

import com.pharmacy.dto.product_dto.CategoryResponseDto;
import com.pharmacy.dto.product_dto.ProductResponseDto;
import com.pharmacy.dto.product_dto.ProductSearchResponseDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.ProductMapper;
import com.pharmacy.model.Product;
import com.pharmacy.model.ProductCategory;
import com.pharmacy.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductSearchResponseDto> searchProducts(String name) {
        List<Product> products = productRepository.findByNameContaining(name);
        return productMapper.toSearchResponseDtoList(products);
    }

    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return productMapper.toResponseDto(product);
    }

    public List<CategoryResponseDto> getCategories() {
        return Arrays.stream(ProductCategory.values())
                .map(category -> switch (category) {
                    case MEDICINE -> new CategoryResponseDto(category, "Лекарственные препараты");
                    case VITAMIN -> new CategoryResponseDto(category, "Витамины и БАДы");
                    case DEVICE -> new CategoryResponseDto(category, "Медицинские приборы");
                })
                .toList();
    }

    public List<ProductSearchResponseDto> searchProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findByCategory(category);
        return productMapper.toSearchResponseDtoList(products);
    }
}
