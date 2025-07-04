package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.product.Product;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class CartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%s, %d", product.getCode(), quantity);
    }
}
