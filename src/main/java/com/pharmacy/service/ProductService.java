package com.pharmacy.service;

import com.pharmacy.dto.product_dto.ProductResponseDto;
import com.pharmacy.dto.product_dto.ProductSearchResponseDto;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.ProductMapper;
import com.pharmacy.model.Product;
import com.pharmacy.repository.ProductRepository;
import org.springframework.stereotype.Service;

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
}
