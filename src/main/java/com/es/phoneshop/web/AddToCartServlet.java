package com.es.phoneshop.web;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
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

public class AddToCartServlet extends HttpServlet {
    public static final String CART = "/cart?message=Product added to cart";
    public static final String QUANTITY = "quantity";
    public static final String PRODUCT_ID = "productId";
    public static final String ERROR = "error";

    public static final String WEB_INF_PAGES_PRODUCT_LIST_JSP = "/WEB-INF/pages/productList.jsp";
    public static final String INVALID_PRODUCT_ID_FORMAT = "Invalid product ID format";
    public static final String PRODUCT_ID_OR_QUANTITY_IS_MISSING = "Product ID or quantity is missing";

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productIdParam = request.getParameter(PRODUCT_ID);
        String quantityParam = request.getParameter(QUANTITY);

        if (productIdParam == null || quantityParam == null) {
            handleInvalidInput(request, response, PRODUCT_ID_OR_QUANTITY_IS_MISSING);
            return;
        }

        long productId;
        try {
            productId = ValidationUtils.validateProductId(productIdParam);
        } catch (NumberFormatException e) {
            handleInvalidInput(request, response, INVALID_PRODUCT_ID_FORMAT);
            return;
        }

        int quantity;
        try {
            quantity = ValidationUtils.validateAndParseQuantity(quantityParam, request.getLocale());
        } catch (ParseException | NonPositiveQuantityException e) {
            handleInvalidInput(request, response, e.getMessage());
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
            response.sendRedirect(request.getContextPath() + CART);
        } catch (OutOfStockException e) {
            handleInvalidInput(request, response, e.getMessage());
        }
    }

    private void handleInvalidInput(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException {
        request.setAttribute(ERROR, errorMessage);
        ProductListPageService.getInstance().fillRequestWithProducts(request);
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_LIST_JSP).forward(request, response);
    }
}
