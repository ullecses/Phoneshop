package com.es.phoneshop.model.product;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;

public enum SortField {
    DESCRIPTION(Comparator.comparing(Product::getDescription, Comparator.nullsLast(String::compareToIgnoreCase))),
    PRICE(Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()))),
    DEFAULT(Comparator.comparingInt(p -> 0));

    private final Comparator<Product> comparator;

    SortField(Comparator<Product> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Product> getComparator(List<String> queryWords) {
        if (this == DESCRIPTION && queryWords != null && !queryWords.getFirst().isEmpty()) {
            return Comparator.comparingInt((Product p) -> countQueryOccurrences(p, queryWords)).reversed();
        }
        return comparator;
    }

    private static int countQueryOccurrences(Product product, List<String> queryWords) {
        String description = product.getDescription().toLowerCase();
        return queryWords.stream()
                .mapToInt(word -> StringUtils.countMatches(description, word))
                .sum();
    }
}
