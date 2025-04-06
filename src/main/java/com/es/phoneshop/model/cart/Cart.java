package com.es.phoneshop.model.cart;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cart implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<CartItem> items;

    private int totalQuantity;
    private BigDecimal totalCost;

    public Cart() {
        this.items = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cart[" + items + "]";
    }
}
