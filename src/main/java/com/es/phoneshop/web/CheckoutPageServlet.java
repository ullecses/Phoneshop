package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.services.OrderService;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.services.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CheckoutPageServlet extends HttpServlet {
    private static final String WEB_INF_PAGES_CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";
    private static final String ORDER = "order";
    private static final String ERRORS = "errors";

    private DefaultCartService cartService;
    private OrderService orderService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
        orderService = DefaultOrderService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        cartService.recalculateCart(cart);
        request.setAttribute(ORDER, orderService.getOrder(cart));
        request.setAttribute("paymentMethods", orderService.getPaymentMethod());
        request.getRequestDispatcher(WEB_INF_PAGES_CHECKOUT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = new HashMap<>();
        setRequiredParameter(request, "firstName", errors, order:: setFirstName);
        setRequiredParameter(request, "lastName", errors, order:: setLastName);
        setRequiredParameter(request, "phone", errors, order:: setPhone);

        setRequiredParameter(request, "deliveryAddress", errors, order:: setDeliveryAddress);
        setPaymentMethod(request, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clearCart(cart);
            response.sendRedirect(request.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            request.setAttribute(ERRORS, errors);
            request.setAttribute(ORDER, order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethod());
            request.getRequestDispatcher(WEB_INF_PAGES_CHECKOUT_JSP).forward(request, response);
        }

    }

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "Value is required");
        }
        else consumer.accept(value);
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value == null || value.isEmpty()) {
            errors.put("paymentMethod", "Value is required");
        }
        else order.setPaymentMethod(PaymentMethod.valueOf(value));
    }
}
