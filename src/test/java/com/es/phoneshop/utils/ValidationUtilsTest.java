package com.es.phoneshop.utils;

import com.es.phoneshop.exception.NonPositiveQuantityException;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void validateAndParseQuantity_validQuantity_shouldReturnParsedQuantity() throws ParseException, NonPositiveQuantityException {
        // Arrange
        String validQuantityStr = "10";
        Locale locale = Locale.US;

        // Act
        int result = ValidationUtils.validateAndParseQuantity(validQuantityStr, locale);

        // Assert
        assertEquals(10, result);
    }

    @Test
    void validateAndParseQuantity_nonPositiveQuantity_shouldThrowNonPositiveQuantityException() {
        // Arrange
        String invalidQuantityStr = "0";
        Locale locale = Locale.US;

        // Act & Assert
        assertThrows(NonPositiveQuantityException.class, () -> ValidationUtils.validateAndParseQuantity(invalidQuantityStr, locale));
    }

    @Test
    void validateAndParseQuantity_invalidQuantityFormat_shouldThrowParseException() {
        // Arrange
        String invalidQuantityStr = "invalid";
        Locale locale = Locale.US;

        // Act & Assert
        assertThrows(ParseException.class, () -> ValidationUtils.validateAndParseQuantity(invalidQuantityStr, locale));
    }

    @Test
    void validateProductId_validProductId_shouldReturnParsedId() {
        // Arrange
        String validIdStr = "12345";

        // Act
        long result = ValidationUtils.validateProductId(validIdStr);

        // Assert
        assertEquals(12345L, result);
    }

    @Test
    void validateProductId_negativeProductId_shouldThrowNumberFormatException() {
        // Arrange
        String invalidIdStr = "-12345";

        // Act & Assert
        NumberFormatException exception = assertThrows(NumberFormatException.class, () -> ValidationUtils.validateProductId(invalidIdStr));
        assertEquals("Product ID must be positive", exception.getMessage());
    }

    @Test
    void parseQuantity_validQuantity_shouldReturnParsedQuantity() throws ParseException {
        // Arrange
        String validQuantityStr = "5";
        Locale locale = Locale.GERMANY;

        // Act
        int result = ValidationUtils.parseQuantity(validQuantityStr, locale);

        // Assert
        assertEquals(5, result);
    }

    @Test
    void parseQuantity_invalidQuantityFormat_shouldThrowParseException() {
        // Arrange
        String invalidQuantityStr = "five";
        Locale locale = Locale.GERMANY;

        // Act & Assert
        assertThrows(ParseException.class, () -> ValidationUtils.parseQuantity(invalidQuantityStr, locale));
    }

    @Test
    void parseQuantity_nonPositiveQuantity_shouldThrowNonPositiveQuantityException() {
        // Arrange
        String invalidQuantityStr = "-5";
        Locale locale = Locale.US;

        // Act & Assert
        assertThrows(NonPositiveQuantityException.class, () -> ValidationUtils.parseQuantity(invalidQuantityStr, locale));
    }
}
