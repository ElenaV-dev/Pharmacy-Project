package com.pharmacy.service;

import com.pharmacy.dto.product_dto.*;
import com.pharmacy.exception.EntityNotFoundException;
import com.pharmacy.mapper.ProductMapper;
import com.pharmacy.model.Product;
import com.pharmacy.model.ProductCategory;
import com.pharmacy.repository.CartItemRepository;
import com.pharmacy.repository.ProductRepository;
import com.pharmacy.validator.ProductValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final ProductValidator productValidator;
    private final CartItemRepository cartItemRepository;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper, ProductValidator productValidator, CartItemRepository cartItemRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.productValidator = productValidator;
        this.cartItemRepository = cartItemRepository;
    }

    public List<ProductSearchResponseDto> searchProducts(String name) {
        List<Product> products = productRepository.findByNameContaining(name);
        return productMapper.toSearchResponseDtoList(products);
    }

    public ProductResponseDto findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден с данным id: " + id));
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

    public ProductCreateResponseDto create(ProductCreateRequestDto dto) {
        productValidator.validateCreateDto(dto);
        Product product = productMapper.productCreateDtoToEntity(dto);
        productRepository.save(product);
        log.info("New product created: name={}", product.getName());

        return productMapper.toProductCreateResponseDto(product);
    }

    public ProductCreateResponseDto update(Long id, ProductUpdateDto dto) {
        productValidator.validateUpdateDto(id, dto);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден с данным id: " + id));

        productMapper.updateEntityFromDto(product, dto);
        productRepository.save(product);
        log.info("Product updated: id={}, name={}", product.getId(), product.getName());

        return productMapper.toProductCreateResponseDto(product);
    }

    @Transactional
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Продукт не найден с данным id: " + id));

        cartItemRepository.deleteAllByProductId(id);
        productRepository.delete(product);
        log.info("Product deleted: id={}, name={}", product.getId(), product.getName());
    }
}
