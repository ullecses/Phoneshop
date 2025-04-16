package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import com.es.phoneshop.utils.ValidationUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class DeleteCartItemServlet extends HttpServlet {
    private static final String PRODUCT_ID_IS_MISSING = "Product ID is missing";
    private static final String CART_MESSAGE_CART_ITEM_REMOVED_SUCCESSFULLY = "/cart?message=Cart item removed successfully";
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, PRODUCT_ID_IS_MISSING);
            return;
        }
        try {
            long productId = ValidationUtils.validateProductId(pathInfo.substring(1));
            Cart cart = cartService.getCart(request);
            cartService.delete(cart, productId);

            response.sendRedirect(request.getContextPath() + CART_MESSAGE_CART_ITEM_REMOVED_SUCCESSFULLY);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
