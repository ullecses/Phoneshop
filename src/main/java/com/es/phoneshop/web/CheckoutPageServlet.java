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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CheckoutPageServlet extends HttpServlet {
    private static final String WEB_INF_PAGES_CHECKOUT_JSP = "/WEB-INF/pages/checkout.jsp";
    private static final String ORDER = "order";
    private static final String ERRORS = "errors";
    public static final String ORDER_OVERVIEW = "/order/overview/";
    public static final String PAYMENT_METHODS = "paymentMethods";
    public static final String PAYMENT_METHOD = "paymentMethod";
    public static final String VALUE_IS_REQUIRED = "Value is required";
    public static final String LAST_NAME = "lastName";
    public static final String FIRST_NAME = "firstName";
    public static final String PHONE = "phone";
    public static final String DELIVERY_ADDRESS = "deliveryAddress";

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

        Map<String, String> errors = new HashMap<>();
        setRequiredParameter(request, FIRST_NAME, errors, order:: setFirstName);
        setRequiredParameter(request, LAST_NAME, errors, order:: setLastName);
        setRequiredParameter(request, PHONE, errors, order:: setPhone);

        setRequiredParameter(request, DELIVERY_ADDRESS, errors, order:: setDeliveryAddress);
        setPaymentMethod(request, errors, order);

        if (errors.isEmpty()) {
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

    private void setRequiredParameter(HttpServletRequest request, String parameter, Map<String, String> errors, Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, VALUE_IS_REQUIRED);
        }
        else consumer.accept(value);
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter(PAYMENT_METHOD);
        if (value == null || value.isEmpty()) {
            errors.put(PAYMENT_METHOD, VALUE_IS_REQUIRED);
        }
        else order.setPaymentMethod(PaymentMethod.valueOf(value));
    }

    private Map<String, String> validateOrder(HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String phone = request.getParameter("phone");
        String deliveryDateStr = request.getParameter("deliveryDate");
        String deliveryAddress = request.getParameter("deliveryAddress");
        String paymentMethod = request.getParameter("paymentMethod");

        if (firstName == null || firstName.trim().isEmpty()) {
            errors.put("firstName", "First name is required");
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            errors.put("lastName", "Last name is required");
        }

        if (phone == null || phone.trim().isEmpty() || !phone.matches("^\\+?[0-9]{7,15}$")) {
            errors.put("phone", "Valid phone number is required");
        }

        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            errors.put("deliveryAddress", "Delivery address is required");
        }

        if (deliveryDateStr == null || deliveryDateStr.trim().isEmpty()) {
            errors.put("deliveryDate", "Delivery date is required");
        } else {
            try {
                LocalDate deliveryDate = LocalDate.parse(deliveryDateStr);
                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusDays(60);

                if (deliveryDate.isBefore(today) || deliveryDate.isAfter(maxDate)) {
                    errors.put("deliveryDate", "Delivery date must be within the next 60 days");
                }
            } catch (DateTimeParseException e) {
                errors.put("deliveryDate", "Invalid delivery date format");
            }
        }

        // Валидация метода оплаты
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            errors.put("paymentMethod", "Payment method is required");
        }

        return errors;
    }

}
