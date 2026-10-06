package com.pharmacy.validator;

import com.pharmacy.constants.ErrorMessageConstants;
import com.pharmacy.constants.ValidationFieldConstants;
import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.Product;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public void validateQuantity(int quantity, Product product) {
        if (quantity > product.getStock()) {
            throw new ValidationException(
                    ValidationFieldConstants.QUANTITY,
                    String.format(ErrorMessageConstants.PRODUCT_STOCK_EXCEEDED, product.getStock()));
        }
    }
}
