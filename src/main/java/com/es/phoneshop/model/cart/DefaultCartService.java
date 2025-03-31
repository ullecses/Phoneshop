package com.es.phoneshop.model.cart;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";

    private ProductDao productDao;

    public DefaultCartService() {
        productDao = ArrayListProductDao.getInstance();
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
                throw new OutOfStockException(product, quantity, product.getStock());
            }

            for (CartItem item : cart.getItems()) {
                if (item.getProduct().getId() == productId) {
                    int newQuantity = item.getQuantity() + quantity;
                    if (newQuantity > product.getStock()) {
                        throw new OutOfStockException(product, newQuantity, product.getStock());
                    }
                    item.setQuantity(newQuantity);
                    return;
                }
            }

            cart.getItems().add(new CartItem(product, quantity));
        }
    }
}
