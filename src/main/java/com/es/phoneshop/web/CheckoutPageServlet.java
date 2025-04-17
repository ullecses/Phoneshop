package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.services.DefaultCartService;
import com.es.phoneshop.services.DefaultOrderService;
import com.es.phoneshop.services.OrderService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class CheckoutPageServlet extends HttpServlet {
    public static final String DD_MM_YYYY        = "dd.MM.yyyy";
    public static final String DELIVERY_ADDRESS  = "deliveryAddress";
    public static final String DELIVERY_DATE     = "deliveryDate";
    public static final String FIRST_NAME        = "firstName";
    public static final String LAST_NAME         = "lastName";
    public static final String ORDER_OVERVIEW    = "/order/overview/";
    public static final String PAYMENT_METHOD    = "paymentMethod";
    public static final String PAYMENT_METHODS   = "paymentMethods";
    public static final String PHONE             = "phone";

    private static final String ERRORS                   = "errors";
    private static final String ORDER                    = "order";
    private static final String WEB_INF_PAGES_CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DD_MM_YYYY);

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
        request.setAttribute(PAYMENT_METHODS, orderService.getPaymentMethod());
        request.getRequestDispatcher(WEB_INF_PAGES_CHECKOUT_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = cartService.getCart(request);
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = orderService.validateOrder(request);

        if (errors.isEmpty()) {
            fillOrderFromRequest(request, order);

            orderService.placeOrder(order);
            cartService.clearCart(cart);
            response.sendRedirect(request.getContextPath() + ORDER_OVERVIEW + order.getSecureId());
        } else {
            request.setAttribute(ERRORS, errors);
            request.setAttribute(ORDER, order);
            request.setAttribute(PAYMENT_METHODS, orderService.getPaymentMethod());
            request.getRequestDispatcher(WEB_INF_PAGES_CHECKOUT_JSP).forward(request, response);
        }
    }

    private void fillOrderFromRequest(HttpServletRequest request, Order order) {
        order.setFirstName(request.getParameter(FIRST_NAME));
        order.setLastName(request.getParameter(LAST_NAME));
        order.setPhone(request.getParameter(PHONE));
        order.setDeliveryAddress(request.getParameter(DELIVERY_ADDRESS));

        order.setDeliveryDate(LocalDate.parse(request.getParameter(DELIVERY_DATE), DATE_FORMATTER));

        order.setPaymentMethod(PaymentMethod.valueOf(request.getParameter(PAYMENT_METHOD)));
    }
}
