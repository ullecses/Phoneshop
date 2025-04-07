package com.es.phoneshop.web;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    public static final String WEB_INF_PAGES_CART_JSP = "/WEB-INF/pages/cart.jsp";

    private DefaultCartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        cartService.recalculateCart(cart);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher(WEB_INF_PAGES_CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");

        Map<Long, String> errors = new HashMap<>();

        if (productIds == null || quantities == null || productIds.length != quantities.length) {
            request.setAttribute("errors", Map.of(-1L, "Invalid form submission"));
            doGet(request, response);
            return;
        }

        for (int i = 0; i < productIds.length; i++) {
            try {
                long productId = Long.parseLong(productIds[i]);
                int quantity = getQuantity(quantities[i], request);
                cartService.update(cartService.getCart(request), productId, quantity);
            } catch (NumberFormatException e) {
                errors.put(-1L, "Invalid product ID format");
            } catch (ParseException e) {
                errors.put(Long.parseLong(productIds[i]), "Invalid quantity format");
            } catch (OutOfStockException | NonPositiveQuantityException e) {
                errors.put(Long.parseLong(productIds[i]), e.getMessage());
            }
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");
        } else {
            request.setAttribute("errors", errors);
            doGet(request, response);
        }
    }

    public static int getQuantity(String quantityString, HttpServletRequest request) throws ParseException {
        NumberFormat numberFormat = getNumberFormat(request.getLocale());
        return numberFormat.parse(quantityString).intValue();
    }

    protected static NumberFormat getNumberFormat(Locale locale) {
        return NumberFormat.getInstance(locale);
    }
}
