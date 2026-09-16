package com.pharmacy.validator;

import com.pharmacy.exception.ValidationException;
import com.pharmacy.model.Product;
import org.springframework.stereotype.Component;

@Component
public class CartValidator {

    public void validateQuantity(int quantity, Product product) {
        if(quantity > product.getStock()) {
            throw new ValidationException("quantity", "Only " + product.getStock() + " items are available");
        }
    }
}
