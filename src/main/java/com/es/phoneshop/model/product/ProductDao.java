package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ProductDao {
    Product getProduct(Long id) throws ProductNotFoundException;
    ArrayList<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);
    void save(Product product);
    void delete(Long id);
    Map<Long, List<PriceHistory>> getPriceHistory();
    void clear();
}
