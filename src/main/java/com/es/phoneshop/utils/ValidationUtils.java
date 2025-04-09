package com.es.phoneshop.utils;

import com.es.phoneshop.exception.NonPositiveQuantityException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ValidationUtils {

    public static final String PRODUCT_ID_MUST_BE_POSITIVE = "Product ID must be positive";

    public static int validateAndParseQuantity(String quantityStr, Locale locale) throws ParseException, NonPositiveQuantityException, ParseException {
        int quantity = parseQuantity(quantityStr, locale);
        if (quantity <= 0) {
            throw new NonPositiveQuantityException();
        }
        return quantity;
    }

    public static long validateProductId(String idStr) throws NumberFormatException {
        long id = Long.parseLong(idStr);
        if (id < 0) throw new NumberFormatException(PRODUCT_ID_MUST_BE_POSITIVE);
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
