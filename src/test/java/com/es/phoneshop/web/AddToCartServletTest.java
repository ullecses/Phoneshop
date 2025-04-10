package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.CartService;
import com.es.phoneshop.utils.ValidationUtils;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.text.ParseException;
import java.util.Locale;

import static org.mockito.Mockito.*;

class AddToCartServletTest {

    private AddToCartServlet servlet;
    private CartService cartService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private Cart cart;
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AddToCartServlet();
        cartService = mock(CartService.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        cart = mock(Cart.class);
        dispatcher = mock(RequestDispatcher.class);

        servlet.init(mock(ServletConfig.class));
        var field = AddToCartServlet.class.getDeclaredField("cartService");
        field.setAccessible(true);
        field.set(servlet, cartService);
    }

    @Test
    void doPost_validRequest_shouldAddToCartAndRedirect() throws Exception {
        // Arrange
        try (MockedStatic<ValidationUtils> utils = mockStatic(ValidationUtils.class)) {
            long productId = 1L;
            int quantity = 2;

            when(request.getParameter("productId")).thenReturn("1");
            when(request.getParameter("quantity")).thenReturn("2");
            when(request.getLocale()).thenReturn(Locale.ENGLISH);
            when(cartService.getCart(request)).thenReturn(cart);
            utils.when(() -> ValidationUtils.validateProductId("1")).thenReturn(productId);
            utils.when(() -> ValidationUtils.validateAndParseQuantity("2", Locale.ENGLISH)).thenReturn(quantity);
            when(request.getContextPath()).thenReturn("");

            // Act
            servlet.doPost(request, response);

            // Assert
            verify(cartService).add(cart, productId, quantity);
            verify(response).sendRedirect("/cart?message=Product added to cart");
        }
    }

    @Test
    void doPost_parseException_shouldForwardWithError() throws Exception {
        // Arrange
        try (MockedStatic<ValidationUtils> utils = mockStatic(ValidationUtils.class)) {
            long productId = 1L;

            when(request.getParameter("productId")).thenReturn("1");
            when(request.getParameter("quantity")).thenReturn("abc");
            when(request.getLocale()).thenReturn(Locale.ENGLISH);
            when(cartService.getCart(request)).thenReturn(cart);

            utils.when(() -> ValidationUtils.validateProductId("1")).thenReturn(productId);
            utils.when(() -> ValidationUtils.validateAndParseQuantity("abc", Locale.ENGLISH))
                    .thenThrow(new ParseException("invalid", 0));

            when(request.getRequestDispatcher("/WEB-INF/pages/product.jsp")).thenReturn(dispatcher);

            // Act
            servlet.doPost(request, response);

            // Assert
            verify(request).setAttribute(eq("error"), eq("Invalid quantity format"));
            verify(dispatcher).forward(request, response);
        }
    }

    @Test
    void doPost_outOfStockException_shouldForwardWithError() throws Exception {
        // Arrange
        try (MockedStatic<ValidationUtils> utils = mockStatic(ValidationUtils.class)) {
            long productId = 1L;
            int quantity = 100;

            when(request.getParameter("productId")).thenReturn("1");
            when(request.getParameter("quantity")).thenReturn("100");
            when(request.getLocale()).thenReturn(Locale.ENGLISH);
            when(cartService.getCart(request)).thenReturn(cart);

            utils.when(() -> ValidationUtils.validateProductId("1")).thenReturn(productId);
            utils.when(() -> ValidationUtils.validateAndParseQuantity("100", Locale.ENGLISH)).thenReturn(quantity);

            doThrow(new OutOfStockException()).when(cartService).add(cart, productId, quantity);
            when(request.getRequestDispatcher("/WEB-INF/pages/product.jsp")).thenReturn(dispatcher);

            // Act
            servlet.doPost(request, response);

            // Assert
            verify(request).setAttribute(eq("error"), eq("There are not so products"));
            verify(dispatcher).forward(request, response);
        }
    }
}