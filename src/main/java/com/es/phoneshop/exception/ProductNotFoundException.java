package com.es.phoneshop.exception;

public class ProductNotFoundException extends RuntimeException {
    private Long productId;

    public ProductNotFoundException(Long productId) {
        super("Product with ID " + productId + " not found");
        this.productId = productId;
    }
}
