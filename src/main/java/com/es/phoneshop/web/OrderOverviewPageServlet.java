package com.es.phoneshop.web;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private static final String WEB_INF_PAGES_OVERVIEW_JSP = "/WEB-INF/pages/orderOverview.jsp";
    public static final String ORDER = "order";
    public static final String SECURE_ID_IS_MISSING = "Secure ID is missing";
    private static final String WEB_INF_PAGES_ERROR_JSP = "/WEB-INF/pages/error.jsp";
    public static final String ERROR_MESSAGE = "errorMessage";

    private OrderDao orderDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, SECURE_ID_IS_MISSING);
            return;
        }

        String secureId = pathInfo.substring(1);
        try {
            Order order = orderDao.getOrderBySecureId(secureId);
            request.setAttribute(ORDER, order);
            request.getRequestDispatcher(WEB_INF_PAGES_OVERVIEW_JSP).forward(request, response);
        } catch (OrderNotFoundException e) {
            request.setAttribute(ERROR_MESSAGE, e.getMessage());
            request.getRequestDispatcher(WEB_INF_PAGES_ERROR_JSP).forward(request, response);
        } catch (Exception e) {
        }
    }
}
