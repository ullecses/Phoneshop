package com.es.phoneshop.web;

import com.es.phoneshop.model.order.ArrayListOrderDao;
import com.es.phoneshop.model.order.OrderDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {
    private static final String WEB_INF_PAGES_OVERVIEW_JSP = "/WEB-INF/pages/orderOverview.jsp";

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
}
