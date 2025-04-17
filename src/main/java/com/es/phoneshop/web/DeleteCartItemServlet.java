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
        try {
            String extractedId = ValidationUtils.extractIdFromPath(pathInfo);
            long productId = ValidationUtils.validateProductId(extractedId);

            Cart cart = cartService.getCart(request);
            cartService.delete(cart, productId);

            response.sendRedirect(request.getContextPath() + CART_MESSAGE_CART_ITEM_REMOVED_SUCCESSFULLY);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

}
