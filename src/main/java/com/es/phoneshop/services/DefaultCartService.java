package com.es.phoneshop.services;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private final ProductDao productDao;

    private static volatile DefaultCartService instance;

    public DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public DefaultCartService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public static DefaultCartService getInstance() {
        if (instance == null) {
            synchronized (DefaultCartService.class) {
                if (instance == null) {
                    instance = new DefaultCartService();
                }
            }
        }
        return instance;
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cart cart = (Cart)request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            cart = new Cart();
            session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
        }
        return cart;
    }

    @Override
    public void add(Cart cart, long productId, int quantity) {
        synchronized (cart) {
            Product product = productDao.getProduct(productId);
            if (product == null) {
                throw new ProductNotFoundException(productId);
            }
            if (product.getStock() < quantity) {
                throw new OutOfStockException(product.getId());
            }

            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId() == productId) {
                    int newQuantity = item.getQuantity() + quantity;
                    if (newQuantity > product.getStock()) {
                        throw new OutOfStockException(product.getId());
                    }
                    item.setQuantity(newQuantity);
                    return;
                }
            }

            cart.getItems().add(new CartItem(product, quantity));
        }
        recalculateCart(cart);
    }

    @Override
    public void update(Cart cart, long productId, int quantity) {
        if (quantity < 0) {
            throw new NonPositiveQuantityException();
        }

        Product product = productDao.getProduct(productId);

        if (product.getStock() < quantity) {
            throw new OutOfStockException(product.getId());
        }

        findCartItemForUpdate(cart, productId, quantity)
                .ifPresentOrElse(
                        item -> item.setQuantity(quantity),
                        () -> cart.getItems().add(new CartItem(product, quantity))
                );

        recalculateCart(cart);
    }

    @Override
    public void delete(Cart cart, Long productId) {
        cart.getItems().removeIf(item ->
                productId.equals(item.getProduct().getId()));
        recalculateCart(cart);
    }

    @Override
    public void clearCart(Cart cart) {
        synchronized (cart) {
            cart.getItems().clear();
            cart.setTotalQuantity(0);
            cart.setTotalCost(BigDecimal.ZERO);
        }
    }

    private Optional<CartItem> findCartItemForUpdate(Cart cart, long productId, int quantity) {
        if (quantity <= 0) {
            throw new NonPositiveQuantityException();
        }

        Product product = productDao.getProduct(productId);
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(c -> c.getProduct().getId().equals(productId))
                .findFirst();

        int productsAmount = cartItemOptional.map(CartItem::getQuantity).orElse(0);

        if (product.getStock() < productsAmount + quantity) {
            throw new OutOfStockException(product.getId());
        }

        return cartItemOptional;
    }

    public void recalculateCart(Cart cart) {
        List<CartItem> items = cart.getItems();

        int totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        cart.setTotalQuantity(totalQuantity);

        BigDecimal totalCost = items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        cart.setTotalCost(totalCost);
    }
}
