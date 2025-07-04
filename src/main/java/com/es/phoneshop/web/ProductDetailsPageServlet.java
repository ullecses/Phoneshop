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
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.es.phoneshop.utils.ValidationUtils.parseQuantity;

public class ProductDetailsPageServlet extends HttpServlet {

    public static final String QUANTITY = "quantity";
    public static final String PRODUCT_ID = "productId";
    public static final String CART = "cart";
    public static final String ERROR = "error";
    public static final String PRODUCT = "product";
    public static final String MESSAGE = "message";
    public static final String ERROR_MESSAGE = "errorMessage";

    public static final String WEB_INF_PAGES_PRODUCT_JSP = "/WEB-INF/pages/product.jsp";
    public static final String WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP = "/WEB-INF/pages/product-not-found.jsp";
    public static final String WEB_INF_PAGES_ERROR_JSP = "/WEB-INF/pages/error.jsp";

    public static final String RECENT_PRODUCTS = "recentProducts";
    public static final String PRODUCT_ADDED_TO_CART = "Product added to cart";

    public static final String INVALID_PRODUCT_ID = "Invalid product ID";
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

            HttpSession session = request.getSession();
            List<Product> recentProducts = (List<Product>) session.getAttribute(RECENT_PRODUCTS);

            if (recentProducts == null) {
                recentProducts = new ArrayList<>();
            } else {
                recentProducts = new ArrayList<>(recentProducts);
            }

            recentProducts.removeIf(p -> p.getId() == productId);

            recentProducts.add(0, product);

            if (recentProducts.size() > 4) {
                recentProducts = new ArrayList<>(recentProducts.subList(0, 4));
            }

            session.setAttribute(RECENT_PRODUCTS, recentProducts);

            List<Product> displayRecentProducts = recentProducts.stream()
                    .filter(p -> p.getId() != productId)
                    .limit(3)
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
        String productInfo = request.getPathInfo().substring(1);
        long productId = Long.valueOf(productInfo);
        String quantityStr = request.getParameter(QUANTITY);

        int quantity;
        Locale locale = request.getLocale();
        try {
            quantity = parseQuantity(quantityStr, locale);
        } catch (ParseException | NumberFormatException | NonPositiveQuantityException e) {
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
            request.setAttribute(QUANTITY, quantity);
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
