package com.es.phoneshop.exception;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(Long productId) {
        super("There are not so products with id " + productId);
    }

    public OutOfStockException() {
        super("There are not so products");
    }
}
