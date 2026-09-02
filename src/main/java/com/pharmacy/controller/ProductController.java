package com.pharmacy.controller;

import com.pharmacy.dto.product_dto.ProductResponseDto;
import com.pharmacy.dto.product_dto.ProductSearchResponseDto;
import com.pharmacy.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public List<ProductSearchResponseDto> searchProducts(@RequestParam String name) {
        return productService.searchProducts(name);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getProductById(@PathVariable Long id) {
        return productService.findById(id);
    }
}
