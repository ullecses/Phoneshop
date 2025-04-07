package com.es.phoneshop.web;

import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MiniCartServlet extends HttpServlet {
    public static final String WEB_INF_PAGES_MINICART_JSP = "/WEB-INF/pages/minicart.jsp";

    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("cart", cartService.getCart(request));
        request.getRequestDispatcher(WEB_INF_PAGES_MINICART_JSP).include(request, response);
    }
}
