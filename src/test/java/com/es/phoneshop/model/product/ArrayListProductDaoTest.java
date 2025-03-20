package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    /*@Test
    public void testSaveProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100,
                "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");

        assertNull(product.getId(), "ID нового продукта должен быть равен null");

        productDao.save(product);

        assertNotNull(String.valueOf(product.getId()), "ID продукта не должен быть null после сохранения");
        assertTrue("ID должен быть положительным", product.getId() > 0);

        Product result = productDao.getProduct(product.getId());

        assertNotNull(String.valueOf(result), "Сохранённый продукт не должен быть null");
        assertEquals("sgs", result.getCode(), "Коды продукта должны совпадать");
    }*/
}