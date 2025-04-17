package com.es.phoneshop.services;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.PaymentMethod;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    public static final String DD_MM_YYYY                             = "dd.MM.yyyy";

    public static final String DELIVERY_ADDRESS                      = "deliveryAddress";
    public static final String DELIVERY_ADDRESS_IS_REQUIRED          = "Delivery address is required";
    public static final String DELIVERY_DATE                         = "deliveryDate";
    public static final String DELIVERY_DATE_IS_REQUIRED             = "Delivery date is required";
    public static final String DELIVERY_DATE_MUST_BE_WITHIN_THE_NEXT_60_DAYS = "Delivery date must be within the next 60 days";
    public static final String FIRST_NAME                            = "firstName";
    public static final String FIRST_NAME_IS_REQUIRED                = "First name is required";
    public static final String INVALID_DELIVERY_DATE_FORMAT          = "Invalid delivery date format";
    public static final String LAST_NAME                             = "lastName";
    public static final String LAST_NAME_IS_REQUIRED                 = "Last name is required";
    public static final String PAYMENT_METHOD                        = "paymentMethod";
    public static final String PAYMENT_METHOD_IS_REQUIRED            = "Payment method is required";
    public static final String PHONE                                 = "phone";
    public static final String VALID_PHONE_NUMBER_IS_REQUIRED        = "Valid phone number is required";

    private final DefaultCartService cartService = DefaultCartService.getInstance();
    private static volatile DefaultOrderService instance;
    private final OrderDao orderDao = ArrayListOrderDao.getInstance();

    public static DefaultOrderService getInstance() {
        if (instance == null) {
            synchronized (DefaultOrderService.class) {
                if (instance == null) {
                    instance = new DefaultOrderService();
                }
            }
        }
        return instance;
    }

    @Override
    public Order getOrder(Cart cart) {
        cartService.recalculateCart(cart);
        Order order = new Order();
        order.setItems(cart.getItems().stream().map(item -> {
            try {
                return (CartItem) item.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));
        return order;
    }

    @Override
    public List<PaymentMethod> getPaymentMethod() {
        return List.of(PaymentMethod.values());
    }

    @Override
    public void placeOrder(Order order) {
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(5);
    }

    @Override
    public Map<String, String> validateOrder(HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        String firstName = request.getParameter(FIRST_NAME);
        String lastName = request.getParameter(LAST_NAME);
        String phone = request.getParameter(PHONE);
        String deliveryDateStr = request.getParameter(DELIVERY_DATE);
        String deliveryAddress = request.getParameter(DELIVERY_ADDRESS);
        String paymentMethod = request.getParameter(PAYMENT_METHOD);

        validateRequiredField(errors, firstName, FIRST_NAME, FIRST_NAME_IS_REQUIRED);
        validateRequiredField(errors, lastName, LAST_NAME, LAST_NAME_IS_REQUIRED);
        validateRequiredField(errors, phone, PHONE, VALID_PHONE_NUMBER_IS_REQUIRED);
        validateRequiredField(errors, deliveryAddress, DELIVERY_ADDRESS, DELIVERY_ADDRESS_IS_REQUIRED);
        validateRequiredField(errors, deliveryDateStr, DELIVERY_DATE, DELIVERY_DATE_IS_REQUIRED);
        validateRequiredField(errors, paymentMethod, PAYMENT_METHOD, PAYMENT_METHOD_IS_REQUIRED);

        if (phone != null && !phone.trim().isEmpty() && !phone.matches("^\\+?[0-9]{7,15}$")) {
            errors.put(PHONE, VALID_PHONE_NUMBER_IS_REQUIRED);
        }

        if (deliveryDateStr != null && !deliveryDateStr.trim().isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DD_MM_YYYY);
                LocalDate deliveryDate = LocalDate.parse(deliveryDateStr, formatter);
                LocalDate today = LocalDate.now();
                LocalDate maxDate = today.plusDays(60);

                if (deliveryDate.isBefore(today) || deliveryDate.isAfter(maxDate)) {
                    errors.put(DELIVERY_DATE, DELIVERY_DATE_MUST_BE_WITHIN_THE_NEXT_60_DAYS);
                }
            } catch (DateTimeParseException e) {
                errors.put(DELIVERY_DATE, INVALID_DELIVERY_DATE_FORMAT);
            }
        }

        return errors;
    }

    private void validateRequiredField(Map<String, String> errors, String value, String fieldName, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            errors.put(fieldName, errorMessage);
        }
    }
}
