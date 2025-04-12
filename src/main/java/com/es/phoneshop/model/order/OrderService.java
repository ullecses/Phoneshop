package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    Order getOrder(Cart cart);
}

