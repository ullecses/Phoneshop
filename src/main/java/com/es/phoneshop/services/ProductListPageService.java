package com.es.phoneshop.services;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductListPageService {
    @Getter
    private static final ProductListPageService instance = new ProductListPageService();
    private static final String QUERY = "query";
    private static final String SORT = "sort";
    private static final String ORDER = "order";
    private static final String PRODUCTS = "products";
    private static final String PRICE_HISTORY_MAP = "priceHistoryMap";
    private final ProductDao productDao;

    private ProductListPageService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public void fillRequestWithProducts(HttpServletRequest request) {
        String query = Objects.requireNonNullElse(request.getParameter(QUERY), "");

        String sortFieldParam = request.getParameter(SORT);
        String sortOrderParam = request.getParameter(ORDER);

        SortField sortField = SortField.DEFAULT;
        SortOrder sortOrder = SortOrder.ASC;

        if (sortFieldParam != null) {
            sortField = Arrays.stream(SortField.values())
                    .filter(f -> f.name().equalsIgnoreCase(sortFieldParam))
                    .findFirst()
                    .orElse(SortField.DEFAULT);
        }

        if (sortOrderParam != null) {
            sortOrder = Arrays.stream(SortOrder.values())
                    .filter(o -> o.name().equalsIgnoreCase(sortOrderParam))
                    .findFirst()
                    .orElse(SortOrder.ASC);
        }

        List<Product> products = productDao.findProducts(query, sortField, sortOrder);
        Map<Long, List<PriceHistory>> priceHistoryMap = productDao.getPriceHistory();

        request.setAttribute(PRODUCTS, products);
        request.setAttribute(PRICE_HISTORY_MAP, priceHistoryMap);
    }
}
