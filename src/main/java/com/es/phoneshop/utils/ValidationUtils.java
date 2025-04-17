package com.es.phoneshop.utils;

import com.es.phoneshop.exception.NonPositiveQuantityException;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final String PRODUCT_ID_MUST_BE_POSITIVE = "Product ID must be positive";
    public static final String INVALID_QUANTITY_INPUT = "Invalid quantity input: ";
    public static final String PRODUCT_ID_IS_MISSING_OR_INVALID = "Product ID is missing or invalid";
    public static final String NO_VALID_PRODUCT_ID_FOUND_IN_PATH = "No valid product ID found in path";
    public static final String PRODUCT_ID_IS_MISSING = "Product ID is missing";

    public static int validateAndParseQuantity(String quantityStr, Locale locale) throws ParseException, NonPositiveQuantityException, ParseException {
        int quantity = parseQuantity(quantityStr, locale);
        if (quantity <= 0) {
            throw new NonPositiveQuantityException();
        }
        return quantity;
    }

    public static String extractIdFromPath(String pathInfo) {
        if (pathInfo == null) {
            throw new IllegalArgumentException(PRODUCT_ID_IS_MISSING);
        }
        Matcher matcher = Pattern.compile("\\d+").matcher(pathInfo);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new IllegalArgumentException(NO_VALID_PRODUCT_ID_FOUND_IN_PATH);
        }
    }

    public static long validateProductId(String idStr) {
        try {
            long id = Long.parseLong(idStr);
            if (id < 0) {
                throw new IllegalArgumentException(PRODUCT_ID_MUST_BE_POSITIVE);
            }
            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(PRODUCT_ID_IS_MISSING_OR_INVALID);
        }
    }

    public static int parseQuantity(String quantityStr, Locale locale) throws ParseException {
        NumberFormat format = NumberFormat.getInstance(locale);
        format.setParseIntegerOnly(true);

        ParsePosition parsePosition = new ParsePosition(0);
        Number parsedNumber = format.parse(quantityStr.trim(), parsePosition);

        if (parsedNumber == null || parsePosition.getIndex() != quantityStr.trim().length()) {
            throw new ParseException(INVALID_QUANTITY_INPUT + quantityStr, parsePosition.getIndex());
        }

        int quantity = parsedNumber.intValue();

        if (quantity <= 0) {
            throw new NonPositiveQuantityException();
        }

        return quantity;
    }
}
