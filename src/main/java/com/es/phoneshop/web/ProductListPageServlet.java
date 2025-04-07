package com.es.phoneshop.web;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.services.DefaultCartService;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.es.phoneshop.utils.Constants.CART_MESSAGE_CART_UPDATED_SUCCESSFULLY;
import static com.es.phoneshop.utils.Constants.ERRORS;
import static com.es.phoneshop.utils.Constants.INVALID_FORM_SUBMISSION;
import static com.es.phoneshop.utils.Constants.INVALID_PRODUCT_ID;
import static com.es.phoneshop.utils.Constants.INVALID_QUANTITY_FORMAT;
import static com.es.phoneshop.utils.Constants.PRODUCTS;
import static com.es.phoneshop.utils.Constants.PRODUCT_ID;
import static com.es.phoneshop.utils.Constants.QUANTITY;
import static com.es.phoneshop.utils.Constants.WEB_INF_PAGES_PRODUCT_LIST_JSP;
import static com.es.phoneshop.utils.ValidationUtils.parseQuantity;

public class ProductListPageServlet extends HttpServlet {
    public static final String QUERY = "query";
    public static final String SORT = "sort";
    public static final String ORDER = "order";
    public static final String PRICE_HISTORY_MAP = "priceHistoryMap";

    private ProductDao productDAO;
    private CartService cartService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productDAO = ArrayListProductDao.getInstance();
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = Objects.requireNonNullElse(request.getParameter(QUERY), "");

        String sortFieldParam = request.getParameter(SORT);
        String sortOrderParam = request.getParameter(ORDER);

        SortField sortField = SortField.DEFAULT;
        SortOrder sortOrder = SortOrder.ASC;

        if (sortFieldParam != null) {
            sortField = Arrays.stream(SortField.values())
                    .filter(f -> f.name().equalsIgnoreCase(sortFieldParam))
                    .findFirst()
                    .orElse(SortField.DEFAULT);
        }

        if (sortOrderParam != null) {
            sortOrder = Arrays.stream(SortOrder.values())
                    .filter(o -> o.name().equalsIgnoreCase(sortOrderParam))
                    .findFirst()
                    .orElse(SortOrder.ASC);
        }

        ArrayList<Product> products = productDAO.findProducts(query, sortField, sortOrder);

        Map<Long, List<PriceHistory>> priceHistoryMap = productDAO.getPriceHistory();

        request.setAttribute(PRODUCTS, products);
        request.setAttribute(PRICE_HISTORY_MAP, priceHistoryMap);
        request.getRequestDispatcher(WEB_INF_PAGES_PRODUCT_LIST_JSP).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] productIds = request.getParameterValues(PRODUCT_ID);
        String[] quantities = request.getParameterValues(QUANTITY);

        Map<Long, String> errors = new HashMap<>();

        if (productIds == null || quantities == null || productIds.length != quantities.length) {
            request.setAttribute(ERRORS, Map.of(-1L, INVALID_FORM_SUBMISSION));
            doGet(request, response);
            return;
        }

        for (int i = 0; i < productIds.length; i++) {
            try {
                long productId = Long.parseLong(productIds[i]);
                Locale locale = request.getLocale();
                int quantity = parseQuantity(quantities[i], locale);
                cartService.update(cartService.getCart(request), productId, quantity);
            } catch (NumberFormatException e) {
                errors.put(-1L, INVALID_PRODUCT_ID);
            } catch (ParseException e) {
                errors.put(Long.parseLong(productIds[i]), INVALID_QUANTITY_FORMAT);
            } catch (OutOfStockException | NonPositiveQuantityException e) {
                errors.put(Long.parseLong(productIds[i]), e.getMessage());
            }
        }

        if (errors.isEmpty()) {
            response.sendRedirect(request.getContextPath() + CART_MESSAGE_CART_UPDATED_SUCCESSFULLY);
        } else {
            request.setAttribute(ERRORS, errors);
            doGet(request, response);
        }
    }
}
