package com.es.phoneshop.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String productId) {
        super("Product with ID " + productId + " not found");
    }
}
