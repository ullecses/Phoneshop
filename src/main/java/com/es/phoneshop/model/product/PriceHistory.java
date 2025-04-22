package com.es.phoneshop.model.product;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Date;

@Getter
public class PriceHistory implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Date date;
    private final BigDecimal price;
    private final Currency currency;

    public PriceHistory(Date date, BigDecimal price, Currency currency) {
        this.date = date;
        this.price = price;
        this.currency = currency;
    }
}

