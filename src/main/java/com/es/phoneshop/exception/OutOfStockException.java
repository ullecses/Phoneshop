package com.es.phoneshop.exception;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException(Product product) {
        super("There are not so products with id " + product.getId());
    }

    public OutOfStockException() {
        super("There are not so products");
    }
}
