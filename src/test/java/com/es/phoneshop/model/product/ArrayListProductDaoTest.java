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
import static org.junit.jupiter.api.Assertions.assertNull;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;
    private Product product;

    @Before
    public void setup() {
    }

    @Test
    public void testSaveProduct() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");
        product = new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");

        assertNull(product.getId(), "ID нового продукта должен быть равен null");

        productDao.save(product);

        assertNotNull(String.valueOf(product.getId()), "ID продукта не должен быть null после сохранения");
        assertTrue("ID должен быть положительным", product.getId() > 0);

        Product result = productDao.getProduct(product.getId());

        assertNotNull(String.valueOf(result), "Сохранённый продукт не должен быть null");

        assertEquals("Коды продукта должны совпадать","sgs", result.getCode());
    }

    @Test
    public void testFindProductsByQuery() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");

        productDao.save(new Product("sgs", "Phone A", new BigDecimal(500), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Phone B", new BigDecimal(700), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Smartphone C", new BigDecimal(600), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));


        List<Product> results = productDao.findProducts("Phone", SortField.DESCRIPTION, SortOrder.ASC);
        assertFalse(results.isEmpty(), "Список не должен быть пустым");
        assertTrue(results.stream().allMatch(p -> p.getDescription().matches("(?i).*phone.*")));
    }

    @Test
    public void testFindProductsSortByDescription() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");

        productDao.save(new Product("sgs", "Phone A", new BigDecimal(500), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Phone B", new BigDecimal(700), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Smartphone C", new BigDecimal(600), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));

        List<Product> results = productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC);
        assertEquals(List.of("Phone A", "Phone B", "Smartphone C"), results.stream().map(Product::getDescription).toList());
    }

    @Test
    public void testFindProductsSortByPriceDesc() {
        productDao = ArrayListProductDao.getInstance();
        Currency usd = Currency.getInstance("USD");

        productDao.save(new Product("sgs", "Phone A", new BigDecimal(500), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Phone B", new BigDecimal(700), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        productDao.save(new Product("sgs", "Smartphone C", new BigDecimal(600), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));

        List<Product> results = productDao.findProducts("", SortField.PRICE, SortOrder.DESC);
        List<BigDecimal> expectedPrices = List.of(new BigDecimal(700), new BigDecimal(600), new BigDecimal(500));
        List<BigDecimal> actualPrices = results.stream().map(Product::getPrice).toList();

        assertEquals(expectedPrices, actualPrices);
    }
}