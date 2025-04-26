package com.es.phoneshop.web;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import com.es.phoneshop.utils.ValidationUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProductDetailsPageServlet extends HttpServlet {

    private static final String CART                     = "cart";
    private static final String ERROR                    = "error";
    private static final String ERROR_MESSAGE            = "errorMessage";
    private static final String INVALID_PRODUCT_ID       = "Invalid product ID";
    private static final String MESSAGE                  = "message";
    private static final String PRODUCT                  = "product";
    private static final String PRODUCT_ADDED_TO_CART    = "Product added to cart";
    private static final String PRODUCT_ID               = "productId";
    private static final String QUANTITY                 = "quantity";
    private static final String RECENT_PRODUCTS          = "recentProducts";

    private static final String WEB_INF_PAGES_ERROR_JSP           = "/WEB-INF/pages/error.jsp";
    private static final String WEB_INF_PAGES_PRODUCT_JSP         = "/WEB-INF/pages/product.jsp";
    private static final String WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP = "/WEB-INF/pages/product-not-found.jsp";

    private ProductDao productDAO;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDAO = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();

            String extractedId = ValidationUtils.extractIdFromPath(pathInfo);
            long productId = ValidationUtils.validateProductId(extractedId);

            Product product = productDAO.getProduct(productId);

            if (product == null) {
                throw new ProductNotFoundException(productId);
            }

            request.setAttribute(PRODUCT, product);

            HttpSession session = request.getSession();
            List<Product> recentProducts = new ArrayList<>(
                    Optional.ofNullable((List<Product>) session.getAttribute(RECENT_PRODUCTS))
                            .orElse(Collections.emptyList())
            );

            recentProducts.removeIf(p -> p.getId() == productId);

            recentProducts.add(0, product);

            if (recentProducts.size() > 4) {
                recentProducts = new ArrayList<>(recentProducts.subList(0, 4));
            }

            session.setAttribute(RECENT_PRODUCTS, recentProducts);

            List<Product> displayRecentProducts = recentProducts.stream()
                    .skip(1)
                    .collect(Collectors.toList());

            request.setAttribute(RECENT_PRODUCTS, displayRecentProducts);
            request.setAttribute(CART, cartService.getCart(request));
            request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_JSP).forward(request, response);

        } catch (ProductNotFoundException e) {
            request.setAttribute(PRODUCT_ID, e.getMessage());
            request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP).forward(request, response);
        } catch (Exception e) {
            request.setAttribute(ERROR_MESSAGE, INVALID_PRODUCT_ID);
            request.getRequestDispatcher(WEB_INF_PAGES_ERROR_JSP).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String quantityStr = request.getParameter(QUANTITY);
        Locale locale = request.getLocale();

        long productId;
        int quantity;

        try {
            String idStr = ValidationUtils.extractIdFromPath(pathInfo);
            productId = ValidationUtils.validateProductId(idStr);

            quantity = ValidationUtils.validateAndParseQuantity(quantityStr, locale);
        } catch (IllegalArgumentException | ParseException | NonPositiveQuantityException e) {
            request.setAttribute(ERROR, e.getMessage());
            request.setAttribute(QUANTITY, quantityStr);
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);

        try {
            cartService.add(cart, productId, quantity);
            request.setAttribute(MESSAGE, PRODUCT_ADDED_TO_CART);
        } catch (OutOfStockException e) {
            request.setAttribute(ERROR, e.getMessage());
            request.setAttribute(QUANTITY, quantityStr);
        }

        doGet(request, response);
    }

    protected Product getProduct(long productId) {
        return productDAO.getProduct(productId);
    }

    protected Cart getCart(HttpServletRequest request) {
        return cartService.getCart(request);
    }
}
