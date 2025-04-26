package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface OrderDao {
    Order getOrderBySecureId(String secureId);
    void save(Order order);
}
