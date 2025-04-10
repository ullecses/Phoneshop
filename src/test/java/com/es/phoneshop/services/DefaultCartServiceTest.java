package com.es.phoneshop.services;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultCartServiceTest {
    private DefaultCartService cartService;
    private ProductDao productDao;
    private HttpServletRequest request;
    private HttpSession session;
    private Cart cart;

    @Before
    public void setUp() {
        productDao = mock(ProductDao.class);
        cartService = new DefaultCartService(productDao); // Исправлено, передаем мок

        request = mock(HttpServletRequest.class);
        session = mock(HttpSession.class);
        cart = new Cart();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(cart);
    }

    @Test
    public void testGetCart_CartNotInSession_ShouldCreateNewCart() {
        // Arrange
        when(session.getAttribute(anyString())).thenReturn(null);

        // Act
        Cart result = cartService.getCart(request);

        // Assert
        assertNotNull(result);
        verify(session).setAttribute(anyString(), any(Cart.class));
    }

    @Test
    public void testGetCart_CartExistsInSession_ShouldReturnExistingCart() {
        // Act
        Cart result = cartService.getCart(request);

        // Assert
        assertEquals(cart, result);
    }

    @Test
    public void testAdd_ProductExistsAndStockIsSufficient_ShouldAddToCart() {
        // Arrange
        Product product = new Product("Test Product", "Description", new BigDecimal("100"), Currency.getInstance("USD"), 10, "image.jpg");
        when(productDao.getProduct(1L)).thenReturn(product);

        // Act
        cartService.add(cart, 1L, 2);

        // Assert
        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testAdd_ProductNotFound_ShouldThrowException() {
        // Arrange
        when(productDao.getProduct(1L)).thenReturn(null);

        // Act
        cartService.add(cart, 1L, 2);
    }

    @Test(expected = OutOfStockException.class)
    public void testAdd_QuantityExceedsStock_ShouldThrowException() {
        // Arrange
        Product product = new Product("Test Product", "Description", new BigDecimal("100"), Currency.getInstance("USD"), 1, "image.jpg");
        when(productDao.getProduct(1L)).thenReturn(product);

        // Act
        cartService.add(cart, 1L, 2);
    }
}
