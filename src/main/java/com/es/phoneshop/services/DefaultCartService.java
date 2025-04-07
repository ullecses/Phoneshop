package com.es.phoneshop.services;

import com.es.phoneshop.exception.NegativeStockException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Optional;
import java.util.stream.Collectors;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private final ProductDao productDao;

    public DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public DefaultCartService(ProductDao productDao) {
        this.productDao = productDao;
    }

    private static class SingletonHelper {
        private static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return SingletonHelper.INSTANCE;
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
                throw new OutOfStockException(product);
            }

            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId() == productId) {
                    int newQuantity = item.getQuantity() + quantity;
                    if (newQuantity > product.getStock()) {
                        throw new OutOfStockException(product);
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
    public void update(Cart cart, long productId, int quantity) throws OutOfStockException {
        if (quantity < 0) {
            throw new NegativeStockException();
        }

        Product product = productDao.getProduct(productId);
        Optional<CartItem> cartItemOptional = findCartItemForUpdate(cart, productId, quantity);

        if (product.getStock() < quantity) {
            throw new OutOfStockException(product);
        }

        if (cartItemOptional.isPresent()) {
            cartItemOptional.get().setQuantity(quantity);
        } else {
            cart.getItems().add(new CartItem(product, quantity));
        }
        recalculateCart(cart);
    }

    @Override
    public void delete(Cart cart, Long productId) {
        cart.getItems().removeIf(item ->
                productId.equals(item.getProduct().getId()));
        recalculateCart(cart);
    }

    private Optional<CartItem> findCartItemForUpdate(Cart cart, long productId, int quantity) throws OutOfStockException {
        if (quantity <= 0) {
            throw new NegativeStockException();
        }

        Product product = productDao.getProduct(productId);
        Optional<CartItem> cartItemOptional = cart.getItems().stream()
                .filter(c -> c.getProduct().getId().equals(product.getId()))
                .findAny();

        int productsAmount = cartItemOptional.map(CartItem::getQuantity).orElse(0);

        if (product.getStock() < productsAmount + quantity) {
            throw new OutOfStockException(product);
        }

        return cartItemOptional;
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .map(CartItem::getQuantity)
                .collect(Collectors.summingInt(q -> q.intValue())));
    }
}
