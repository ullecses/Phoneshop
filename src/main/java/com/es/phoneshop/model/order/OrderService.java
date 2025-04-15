package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    Order getOrder(Cart cart);
    List<PaymentMethod> getPaymentMethod();
    void placeOrder(Order order);
}

