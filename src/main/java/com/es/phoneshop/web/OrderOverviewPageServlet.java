package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.DefaultOrderService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import com.es.phoneshop.model.order.OrderService;
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

public class OrderOverviewPageServlet extends HttpServlet {
    public static final String WEB_INF_PAGES_OVERVIEW_JSP = "/WEB-INF/pages/orderOverview.jsp";

    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String secureId = request.getPathInfo().substring(1);
        request.setAttribute("order", orderDao.getOrderBySecureId(secureId));
        request.getRequestDispatcher(WEB_INF_PAGES_OVERVIEW_JSP).forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/overview/" + order.getId());
        } else {
            request.setAttribute(ERRORS, errors);
            request.setAttribute(ORDER, order);
            request.setAttribute("paymentMethods", orderService.getPaymentMethod());
            request.getRequestDispatcher(WEB_INF_PAGES_CHECKOUT_JSP).forward(request, response);
        }

    }
}
