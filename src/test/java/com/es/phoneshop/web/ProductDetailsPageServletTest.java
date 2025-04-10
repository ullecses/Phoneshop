package com.es.phoneshop.web;

import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.services.CartService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductDetailsPageServletTest {
    private ProductDetailsPageServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private RequestDispatcher requestDispatcher;
    private CartService cartService;

    private final long productId = 1L;
    private Product testProduct;

    @BeforeEach
    void setup() throws Exception {
        servlet = new ProductDetailsPageServlet();

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);
        requestDispatcher = mock(RequestDispatcher.class);
        cartService = mock(CartService.class);

        ServletConfig config = mock(ServletConfig.class);
        servlet.init(config);

        when(request.getSession()).thenReturn(session);
        when(request.getLocale()).thenReturn(Locale.ENGLISH);

        testProduct = new Product("code", "desc", new BigDecimal(100), Currency.getInstance("USD"), 10, null);
    }

    @Test
    void testDoGetInvalidProductId() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/abc");
        when(request.getRequestDispatcher(ProductDetailsPageServlet.WEB_INF_PAGES_ERROR_JSP)).thenReturn(requestDispatcher);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("errorMessage"), eq("Invalid product ID"));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    void testDoGetProductNotFound() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/999");
        when(request.getRequestDispatcher(ProductDetailsPageServlet.WEB_INF_PAGES_PRODUCT_NOT_FOUND_JSP)).thenReturn(requestDispatcher);
        servlet = spy(servlet);
        doThrow(new ProductNotFoundException(999L)).when(servlet).getProduct(999L);

        // Act
        servlet.doGet(request, response);

        // Assert
        verify(request).setAttribute(eq("productId"), anyString());
        verify(requestDispatcher).forward(request, response);
    }
}