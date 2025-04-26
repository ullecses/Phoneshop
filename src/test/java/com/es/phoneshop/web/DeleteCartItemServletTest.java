package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.services.CartService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeleteCartItemServletTest {

    private static final String PRODUCT_ID_IS_MISSING = "Product ID is missing";
    private static final String CART_MESSAGE_CART_ITEM_REMOVED_SUCCESSFULLY = "/cart?message=Cart item removed successfully";

    private DeleteCartItemServlet servlet;

    @Mock
    private CartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Cart cart;

    @BeforeEach
    void setUp() throws ServletException {
        MockitoAnnotations.openMocks(this);
        servlet = new DeleteCartItemServlet();
        servlet.init(null);

        when(cartService.getCart(request)).thenReturn(cart);
    }

    @Test
    void doPost_productIdIsMissing_shouldReturnBadRequest() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn(null);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, PRODUCT_ID_IS_MISSING);
    }

    @Test
    void doPost_invalidProductId_shouldReturnBadRequest() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/invalidId");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "For input string: \"invalidId\"");
    }

    @Test
    void doPost_validProductId_shouldDeleteItemAndRedirect() throws Exception {
        // Arrange
        long validProductId = 1L;
        when(request.getPathInfo()).thenReturn("/" + validProductId);
        when(cartService.getCart(request)).thenReturn(cart);

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(cartService).delete(cart, validProductId);
        verify(response).sendRedirect(request.getContextPath() + CART_MESSAGE_CART_ITEM_REMOVED_SUCCESSFULLY);
    }

    @Test
    void doPost_invalidProductIdFormat_shouldReturnBadRequest() throws Exception {
        // Arrange
        when(request.getPathInfo()).thenReturn("/non-numeric-id");

        // Act
        servlet.doPost(request, response);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_BAD_REQUEST, "For input string: \"non-numeric-id\"");
    }
}
