package com.es.phoneshop.exception;

public class NegativeStockException extends RuntimeException {

    public NegativeStockException() {
        super("The quantity of products cannot be less than 1");
    }
}

