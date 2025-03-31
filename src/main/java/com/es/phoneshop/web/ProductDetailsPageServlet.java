package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.cart.DefaultCartService;
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

public class ProductDetailsPageServlet extends HttpServlet {
    public static final String PRODUCT = "product";
    public static final String WEB_INF_PAGES_PRODUCT_JSP = "/WEB-INF/pages/product.jsp";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String PRODUCT_ID = "productId";
    public static final String WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP = "/WEB-INF/pages/product-not-found.jsp";
    public static final String WEB_INF_PAGES_ERROR_JSP = "/WEB-INF/pages/error.jsp";
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
            List<Product> recentProducts = (List<Product>) session.getAttribute("recentProducts");

            if (recentProducts == null) {
                recentProducts = new ArrayList<>();
            } else {
                recentProducts = new ArrayList<>(recentProducts); // Создаем копию для изменения
            }

            recentProducts.remove(product);

            request.setAttribute("cart", cartService.getCart(request));
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
        String quantityStr = request.getParameter("quantity");

        int quantity;
        request.getLocale();
        NumberFormat format = NumberFormat.getInstance(request.getLocale());
        try {
            Number parsedNumber = format.parse(quantityStr);
            quantity = parsedNumber.intValue();
            if (quantity <= 0) {
                throw new NumberFormatException("Quantity must be greater than zero.");
            }
        } catch (ParseException | NumberFormatException e) {
            request.setAttribute("error", "Invalid quantity format");
            request.setAttribute("quantity", quantityStr);
            doGet(request, response);
            return;
        }

        Cart cart = cartService.getCart(request);
        try {
            cartService.add(cart, productId, quantity);
            request.setAttribute("message", "Product added to cart");
        } catch (OutOfStockException e) {
            request.setAttribute("error", "Not enough stock available");
            request.setAttribute("quantity", quantity);
        }
        doGet(request, response);
    }
}
