package com.es.phoneshop.model.order;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;

import java.math.BigDecimal;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {

    private static volatile DefaultOrderService instance;

    public DefaultOrderService() {
        instance = new DefaultOrderService();
    }

    public static DefaultOrderService getInstance() {
        if (instance == null) {
            synchronized (DefaultOrderService.class) {
                if (instance == null) {
                    instance = new DefaultOrderService();
                }
            }
        }
        return instance;
    }

    @Override
    public Order getOrder(Cart cart) {
        Order order = new Order();
        order.setItems(cart.getItems().stream().map(item -> {
            try {
                return (CartItem) item.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(5);
    }
}
