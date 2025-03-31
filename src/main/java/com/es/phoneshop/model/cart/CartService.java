package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import jakarta.servlet.http.HttpServletRequest;

public interface CartService {
    Cart getCart(HttpServletRequest request);
    void add(Cart cart, long productId, int quantity) throws OutOfStockException;
}
