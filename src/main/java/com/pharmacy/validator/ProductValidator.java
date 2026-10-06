package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.dto.product_dto.ProductCreateRequestDto;
import com.pharmacy.dto.product_dto.ProductUpdateDto;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class ProductValidator {

    private final ProductRepository productRepository;

    public ProductValidator(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public void validateCreateDto(ProductCreateRequestDto dto) {
        if (productRepository.existsByName(dto.getName())) {
            throw new ValidationException(
                    ValidationFieldConstants.NAME,
                    ErrorMessageConstants.PRODUCT_EXISTS);
        }
    }

    public void validateUpdateDto(Long id, ProductUpdateDto dto) {
        if (productRepository.existsByNameAndIdNot(dto.getName(), id)) {
            throw new ValidationException(
                    ValidationFieldConstants.NAME,
                    ErrorMessageConstants.PRODUCT_EXISTS);
        }
    }
}
