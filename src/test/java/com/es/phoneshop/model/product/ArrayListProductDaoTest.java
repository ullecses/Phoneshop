package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ArrayListProductDaoTest {
    private ProductDao productDao;
    private Currency usd;

    @Before
    public void setup() {
        productDao = ArrayListProductDao.getInstance();
        usd = Currency.getInstance("USD");
        productDao.clear();
    }

    @Test
    public void testSaveProduct() {
        // Arrange
        Product product = new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");

        // Act
        productDao.save(product);
        Product result = productDao.getProduct(product.getId());

        // Assert
        assertNotNull("ID продукта не должен быть null после сохранения", product.getId());
        assertTrue("ID должен быть положительным", product.getId() > 0);
        assertNotNull("Сохранённый продукт не должен быть null", result);
        assertEquals("Коды продукта должны совпадать", "sgs", result.getCode());
    }

    @Test
    public void testFindProductsByQuery() {
        // Arrange
        productDao.save(new Product("sgs1", "Phone A", new BigDecimal(500), usd, 100, "image1.jpg"));
        productDao.save(new Product("sgs2", "Phone B", new BigDecimal(700), usd, 100, "image2.jpg"));
        productDao.save(new Product("sgs3", "Smartphone C", new BigDecimal(600), usd, 100, "image3.jpg"));

        // Act
        List<Product> results = productDao.findProducts("Phone", SortField.DESCRIPTION, SortOrder.ASC);

        // Assert
        assertFalse(results.isEmpty(), "Список не должен быть пустым");
        assertTrue(results.stream().allMatch(p -> p.getDescription().toLowerCase().contains("phone")));
    }

    @Test
    public void testFindProductsSortByDescription() {
        // Arrange
        productDao.save(new Product("sgs1", "Phone A", new BigDecimal(500), usd, 100, "image1.jpg"));
        productDao.save(new Product("sgs2", "Phone B", new BigDecimal(700), usd, 100, "image2.jpg"));
        productDao.save(new Product("sgs3", "Smartphone C", new BigDecimal(600), usd, 100, "image3.jpg"));

        // Act
        List<Product> results = productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC);

        // Assert
        assertEquals(List.of("Phone A", "Phone B", "Smartphone C"), results.stream().map(Product::getDescription).toList());
    }

    @Test
    public void testFindProductsSortByPriceDesc() {
        // Arrange
        productDao.save(new Product("sgs1", "Phone A", new BigDecimal(500), usd, 100, "image1.jpg"));
        productDao.save(new Product("sgs2", "Phone B", new BigDecimal(700), usd, 100, "image2.jpg"));
        productDao.save(new Product("sgs3", "Smartphone C", new BigDecimal(600), usd, 100, "image3.jpg"));

        // Act
        List<Product> results = productDao.findProducts("", SortField.PRICE, SortOrder.DESC);
        List<BigDecimal> actualPrices = results.stream().map(Product::getPrice).toList();

        // Assert
        List<BigDecimal> expectedPrices = List.of(new BigDecimal(700), new BigDecimal(600), new BigDecimal(500));
        assertEquals(expectedPrices, actualPrices);
    }
}