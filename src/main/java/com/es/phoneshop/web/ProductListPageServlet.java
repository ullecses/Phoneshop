package com.es.phoneshop.web;

import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ProductListPageServlet extends HttpServlet {
    public static final String QUERY = "query";
    public static final String SORT = "sort";
    public static final String ORDER = "order";
    public static final String PRODUCTS = "products";
    public static final String PRICE_HISTORY_MAP = "priceHistoryMap";
    public static final String WEB_INF_PAGES_PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    private ProductDao productDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDAO = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        ArrayList<Product> products = productDAO.findProducts(query, sortField, sortOrder);

        Map<Long, List<PriceHistory>> priceHistoryMap = productDAO.getPriceHistory();

        request.setAttribute(PRODUCTS, products);
        request.setAttribute(PRICE_HISTORY_MAP, priceHistoryMap);
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_LIST_JSP).forward(request, response);
    }
}
