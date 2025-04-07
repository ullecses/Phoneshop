package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import com.es.phoneshop.utils.QuantityUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;

public class AddToCartServlet extends HttpServlet {
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long productId = Long.parseLong(request.getParameter("productId"));
        String quantityStr = request.getParameter("quantity");

        Cart cart = cartService.getCart(request);

        try {
            int quantity = QuantityUtils.parseQuantity(quantityStr, request.getLocale());

            cartService.add(cart, productId, quantity);

            response.sendRedirect(request.getContextPath() + "/cart?message=Product added to cart");
        } catch (ParseException e) {
            request.setAttribute("error", "Invalid quantity format");
            request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
        } catch (OutOfStockException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(request, response);
        }
    }
}
