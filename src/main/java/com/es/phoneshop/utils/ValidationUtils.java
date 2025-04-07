package com.es.phoneshop.utils;

import com.es.phoneshop.exception.NonPositiveQuantityException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ValidationUtils {
    public static int validateAndParseQuantity(String quantityStr, Locale locale) throws ParseException, NonPositiveQuantityException, ParseException {
        int quantity = parseQuantity(quantityStr, locale);
        if (quantity <= 0) {
            throw new NonPositiveQuantityException();
        }
        return quantity;
    }

    public static long validateProductId(String idStr) throws NumberFormatException {
        long id = Long.parseLong(idStr);
        if (id < 0) throw new NumberFormatException("Product ID must be positive");
        return id;
    }

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
