package com.es.phoneshop.web;

import com.es.phoneshop.model.product.*;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductListPageServlet extends HttpServlet {
    private ProductDao productDAO;
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDAO = new ArrayListProductDao();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        if (query == null) {
            query = "";
        }

        String sortFieldParam = request.getParameter("sort");
        String sortOrderParam = request.getParameter("order");

        SortField sortField = null;
        SortOrder sortOrder = null;

        if (sortFieldParam != null) {
            try {
                sortField = SortField.valueOf(sortFieldParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректный параметр сортировки: " + sortFieldParam);
            }
        }

        if (sortOrderParam != null) {
            try {
                sortOrder = SortOrder.valueOf(sortOrderParam.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Некорректное направление сортировки: " + sortOrderParam);
            }
        }

        List<Product> products = productDAO.findProducts(query, sortField, sortOrder);

        request.setAttribute("products", new ArrayList<>(products));
        request.getRequestDispatcher("/WEB-INF/pages/productList.jsp").forward(request, response);
    }
}
