package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String PRODUCT = "product";
    public static final String WEB_INF_PAGES_PRODUCT_JSP = "/WEB-INF/pages/product.jsp";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String PRODUCT_ID = "productId";
    public static final String WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP = "/WEB-INF/pages/product-not-found.jsp";
    public static final String WEB_INF_PAGES_ERROR_JSP = "/WEB-INF/pages/error.jsp";
    public static final String INVALID_PRODUCT_ID = "Invalid product ID";
    private ProductDao productDAO;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDAO = ArrayListProductDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();

            Matcher matcher = Pattern.compile("\\d+").matcher(pathInfo);
            if (!matcher.find()) {
                throw new IllegalArgumentException(INVALID_PRODUCT_ID);
            }

            long productId = Long.parseLong(matcher.group());
            Product product = productDAO.getProduct(productId);

            if (product == null) {
                throw new ProductNotFoundException(productId);
            }

            request.setAttribute(PRODUCT, product);
            request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_JSP).forward(request, response);

        } catch (ProductNotFoundException e) {
            request.setAttribute(PRODUCT_ID, e.getProductId());
            request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP).forward(request, response);
        } catch (Exception e) {
            request.setAttribute(ERROR_MESSAGE, INVALID_PRODUCT_ID);
            request.getRequestDispatcher(WEB_INF_PAGES_ERROR_JSP).forward(request, response);
        }
    }
}
