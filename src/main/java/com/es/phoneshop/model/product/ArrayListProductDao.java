package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

public class ArrayListProductDao implements ProductDao {
    private static volatile ProductDao instance;
    private final List<Product> products;
    private long maxId = 1;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ArrayListProductDao() {
        this.products = new ArrayList<>();
        saveSampleProducts();
    }

    public static ProductDao getInstance() {
        if (instance == null) {
            synchronized (ArrayListProductDao.class) {
                if (instance == null) {
                    instance = new ArrayListProductDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Product getProduct(Long id) throws ProductNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ProductNotFoundException(id));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            String lowerCaseQuery = query.toLowerCase();

            Stream<Product> productStream = products.stream()
                    .filter(product -> product.getDescription().toLowerCase().contains(lowerCaseQuery));

            if (sortField != null) {
                Comparator<Product> comparator = switch (sortField) {
                    case DESCRIPTION -> Comparator.comparing(Product::getDescription, Comparator.nullsLast(String::compareTo));
                    case PRICE -> Comparator.comparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder()));
                };
                if (sortOrder == SortOrder.DESC) {
                    comparator = comparator.reversed();
                }
                productStream = productStream.sorted(comparator);
            }
            return productStream.toList();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Product product) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            if (product.getId() != null) {
                try {
                    Product existingProduct = getProduct(product.getId());
                    int index = products.indexOf(existingProduct);

                    if (existingProduct.getPrice() != null && !existingProduct.getPrice().equals(product.getPrice())) {
                        existingProduct.addPriceHistory(existingProduct.getPrice(), existingProduct.getCurrency());
                    }

                    products.set(index, product);
                } catch (ProductNotFoundException e) {
                    throw new RuntimeException("Не удалось обновить продукт: " + product.getId(), e);
                }
            } else {
                product.setId(maxId++);
                product.addPriceHistory(product.getPrice(), product.getCurrency());
                products.add(product);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void delete(Long id) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            products.removeIf(product -> id.equals(product.getId()));
        } finally {
            writeLock.unlock();
        }
    }

    private void saveSampleProducts(){
        Currency usd = Currency.getInstance("USD");
        save(new Product( "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
        save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
        save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
        save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
        save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
        save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
        save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
        save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
        save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
        save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
        save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
        save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
    }
}
