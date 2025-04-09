package com.es.phoneshop.web;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import com.es.phoneshop.services.ProductListPageService;
import com.es.phoneshop.utils.ValidationUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class ProductListPageServlet extends HttpServlet {
    public static final String QUANTITY = "quantity";
    public static final String PRODUCT_ID = "productId";
    public static final String ERRORS = "errors";

    public static final String WEB_INF_PAGES_PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    public static final String CART_MESSAGE_CART_UPDATED_SUCCESSFULLY = "/cart?message=Cart updated successfully";

    public static final String INVALID_QUANTITY_FORMAT = "Invalid quantity format";
    public static final String INVALID_PRODUCT_ID = "Invalid product ID";
    public static final String INVALID_FORM_SUBMISSION = "Invalid form submission";

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductListPageService.getInstance().fillRequestWithProducts(request);
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_LIST_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter(PRODUCT_ID);
        String quantityParam = request.getParameter(QUANTITY);

        Map<Long, String> errors = new HashMap<>();

        if (productIdParam == null || quantityParam == null) {
            request.setAttribute(ERRORS, Map.of(-1L, INVALID_FORM_SUBMISSION));
            doGet(request, response);
            return;
        }

        long productId = 0;
        try {
            productId = ValidationUtils.validateProductId(productIdParam);
        } catch (NumberFormatException e) {
            errors.put(-1L, INVALID_PRODUCT_ID);
        }

        int quantity = 0;
        try {
            quantity = ValidationUtils.validateAndParseQuantity(quantityParam, request.getLocale());
        } catch (ParseException | NonPositiveQuantityException e) {
            errors.put(productId, INVALID_QUANTITY_FORMAT);
        }

        if (errors.isEmpty()) {
            cartService.update(cartService.getCart(request), productId, quantity);
            response.sendRedirect(request.getContextPath() + CART_MESSAGE_CART_UPDATED_SUCCESSFULLY);
        } else {
            request.setAttribute(ERRORS, errors);
            doGet(request, response);
        }
    }
}
