package com.es.phoneshop.exception;

public class NonPositiveQuantityException extends RuntimeException {

    public NonPositiveQuantityException() {
        super("The quantity of products cannot be less than 1");
    }
}

