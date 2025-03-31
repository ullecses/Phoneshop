package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String WEB_INF_PAGES_PRODUCT_JSP = "/WEB-INF/pages/product.jsp";
    public static final String WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP = "/WEB-INF/pages/product-not-found.jsp";
    public static final String WEB_INF_PAGES_ERROR_JSP = "/WEB-INF/pages/error.jsp";

    public static final String PRODUCT = "product";
    public static final String CART = "cart";
    public static final String PRODUCT_ID = "productId";
    public static final String RECENT_PRODUCTS = "recentProducts";
    public static final String QUANTITY = "quantity";
    public static final String MESSAGE = "message";
    public static final String PRODUCT_ADDED_TO_CART = "Product added to cart";

    public static final String ERROR = "error";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String INVALID_QUANTITY_FORMAT = "Invalid quantity format";
    public static final String NOT_ENOUGH_STOCK_AVAILABLE = "Not enough stock available";
    public static final String INVALID_PRODUCT_ID = "Invalid product ID";
    public static final String QUANTITY_MUST_BE_GREATER_THAN_ZERO = "Quantity must be greater than zero.";

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
            request.setAttribute(PRODUCT_ID, e.getProductId());
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
        request.getLocale();
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        try {
            Number parsedNumber = format.parse(quantityStr);
            quantity = parsedNumber.intValue();
            if (quantity <= 0) {
                throw new NumberFormatException(QUANTITY_MUST_BE_GREATER_THAN_ZERO);
            }
        } catch (ParseException | NumberFormatException e) {
            request.setAttribute(ERROR, INVALID_QUANTITY_FORMAT);
            request.setAttribute(QUANTITY, quantityStr);
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
            request.setAttribute(MESSAGE, PRODUCT_ADDED_TO_CART);
        } catch (OutOfStockException e) {
            request.setAttribute(ERROR, NOT_ENOUGH_STOCK_AVAILABLE);
            request.setAttribute(QUANTITY, quantity);
        }
        doGet(request, response);
    }
}
