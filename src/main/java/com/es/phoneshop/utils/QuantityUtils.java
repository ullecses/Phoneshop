package com.es.phoneshop.utils;

import com.es.phoneshop.exception.NonPositiveQuantityException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class QuantityUtils {

    public static int parseQuantity(String quantityStr, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        Number parsedNumber = format.parse(quantityStr);
        int quantity = parsedNumber.intValue();
        if (quantity <= 0) {
            throw new NonPositiveQuantityException();
        }
        return quantity;
    }
}
