package com.pharmacy.controller;

import com.pharmacy.constants.ApiEndpointConstants;
import com.pharmacy.dto.product_dto.*;
import com.pharmacy.model.ProductCategory;
import com.pharmacy.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpointConstants.PRODUCT)
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(ApiEndpointConstants.PRODUCT_SEARCH)
    public List<ProductSearchResponseDto> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    @GetMapping(ApiEndpointConstants.PRODUCT_BY_ID)
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping(ApiEndpointConstants.PRODUCT_CATEGORIES)
    public List<CategoryResponseDto> getCategories() {
        return productService.getCategories();
    }

    @GetMapping(ApiEndpointConstants.PRODUCT_CATEGORY)
    public List<ProductSearchResponseDto> getProductsByCategory(@PathVariable ProductCategory category) {
        return productService.searchProductsByCategory(category);
    }

    @PostMapping
    public ProductCreateResponseDto createProduct(@Valid @RequestBody ProductCreateRequestDto dto) {
        return productService.create(dto);
    }

    @PutMapping(ApiEndpointConstants.PRODUCT_BY_ID)
    public ProductCreateResponseDto updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDto dto) {

       return productService.update(id, dto);
    }

    @DeleteMapping(ApiEndpointConstants.PRODUCT_BY_ID)
    public void delete(@PathVariable Long id) {
        productService.deleteById(id);
    }
}
