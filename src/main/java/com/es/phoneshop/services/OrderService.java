package com.es.phoneshop.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order getOrder(Cart cart);
    List<PaymentMethod> getPaymentMethod();
    void placeOrder(Order order);
    Map<String, String> validateOrder(HttpServletRequest request);
}

