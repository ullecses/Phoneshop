package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

public class PriceHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date date;
    private BigDecimal price;
    private Currency currency;

    public PriceHistory(Date date, BigDecimal price, Currency currency) {
        this.date = date;
        this.price = price;
        this.currency = currency;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }
}

