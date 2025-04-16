package com.es.phoneshop.web;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.DefaultCartService;
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

public class CartPageServlet extends HttpServlet {
    private static final String WEB_INF_PAGES_CART_JSP = "/WEB-INF/pages/cart.jsp";
    private static final String QUANTITY = "quantity";
    private static final String PRODUCT_ID = "productId";
    private static final String CART = "cart";
    private static final String ERRORS = "errors";;
    private static final String CART_MESSAGE_CART_UPDATED_SUCCESSFULLY = "/cart?message=Cart updated successfully";

    private static final String INVALID_QUANTITY_FORMAT = "Invalid quantity format";
    private static final String INVALID_PRODUCT_ID = "Invalid product ID";
    private static final String INVALID_FORM_SUBMISSION = "Invalid form submission";

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
        request.setAttribute(CART, cart);
        request.getRequestDispatcher(WEB_INF_PAGES_CART_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues(PRODUCT_ID);
        String[] quantities = request.getParameterValues(QUANTITY);

        Map<Long, String> errors = new HashMap<>();

        if (productIds == null || quantities == null || productIds.length != quantities.length) {
            request.setAttribute(ERRORS, Map.of(-1L, INVALID_FORM_SUBMISSION));
            doGet(request, response);
            return;
        }

        for (int i = 0; i < productIds.length; i++) {
            try {
                long productId = ValidationUtils.validateProductId(productIds[i]);
                int quantity = ValidationUtils.validateAndParseQuantity(quantities[i], request.getLocale());
                cartService.update(cartService.getCart(request), productId, quantity);
            } catch (NumberFormatException e) {
                errors.put(-1L, INVALID_PRODUCT_ID);
            } catch (ParseException e) {
                errors.put(Long.parseLong(productIds[i]), INVALID_QUANTITY_FORMAT);
            } catch (OutOfStockException | NonPositiveQuantityException e) {
                errors.put(Long.parseLong(productIds[i]), e.getMessage());
            }
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + CART_MESSAGE_CART_UPDATED_SUCCESSFULLY);
        } else {
            request.setAttribute(ERRORS, errors);
            doGet(request, response);
        }
    }
}
