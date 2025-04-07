package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
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
import java.text.ParseException;

import static com.es.phoneshop.utils.Constants.ERROR;
import static com.es.phoneshop.utils.Constants.INVALID_QUANTITY_FORMAT;
import static com.es.phoneshop.utils.Constants.PRODUCT_ID;
import static com.es.phoneshop.utils.Constants.QUANTITY;
import static com.es.phoneshop.utils.Constants.WEB_INF_PAGES_PRODUCT_JSP;

public class AddToCartServlet extends HttpServlet {
    public static final String CART = "/cart?message=Product added to cart";

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long productId = ValidationUtils.validateProductId(request.getParameter(PRODUCT_ID));
        String quantityStr = request.getParameter(QUANTITY);

        Cart cart = cartService.getCart(request);

        try {
            int quantity = ValidationUtils.validateAndParseQuantity(quantityStr, request.getLocale());

            cartService.add(cart, productId, quantity);

            response.sendRedirect(request.getContextPath() + CART);
        } catch (ParseException e) {
            request.setAttribute(ERROR, INVALID_QUANTITY_FORMAT);
            request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_JSP).forward(request, response);
        } catch (OutOfStockException e) {
            request.setAttribute(ERROR, e.getMessage());
            request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_JSP).forward(request, response);
        }
    }
}
