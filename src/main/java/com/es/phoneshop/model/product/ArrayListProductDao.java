package com.es.phoneshop.model.product;

import com.es.phoneshop.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArrayListProductDao implements ProductDao {
    private static volatile ProductDao instance;
    private final List<Product> products;
    private long maxId = 1;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ArrayListProductDao() {
        this.products = new ArrayList<>();
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
    public ArrayList<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            String lowerCaseQuery = query.toLowerCase();
            List<String> queryWords = Arrays.asList(lowerCaseQuery.split("\\s+"));

            Stream<Product> productStream = products.stream();

            if (!query.isBlank()) {
                productStream = productStream.filter(product -> containsAny(product, queryWords));
            }
            Comparator<Product> comparator = sortField.getComparator(queryWords);
            if (sortOrder == SortOrder.DESC) {
                comparator = comparator.reversed();
            }

            return productStream.sorted(comparator)
                    .collect(Collectors.toCollection(ArrayList::new));
        } finally {
            readLock.unlock();
        }
    }

    private boolean containsAny(Product product, List<String> queryWords) {
        String name = product.getDescription().toLowerCase();
        String description = product.getDescription().toLowerCase();
        return queryWords.stream().anyMatch(word -> name.contains(word) || description.contains(word));
    }

    @Override
    public void save(Product product) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            if (product.getId() != null) {
                Product existingProduct = getProduct(product.getId());
                int index = products.indexOf(existingProduct);

                if (existingProduct.getPrice() != null && !existingProduct.getPrice().equals(product.getPrice())) {
                    existingProduct.setPrice(existingProduct.getPrice(), existingProduct.getCurrency());
                }

                products.set(index, product);
            } else {
                product.setId(maxId++);
                product.setPrice(product.getPrice(), product.getCurrency());
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

    @Override
    public Map<Long, List<PriceHistory>> getPriceHistory() {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            Map<Long, List<PriceHistory>> historyMap = new HashMap<>();
            for (Product product : products) {
                List<PriceHistory> history = product.getPriceHistory();
                historyMap.put(product.getId(), history);
            }
            return historyMap;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void clear() {
        products.clear();
    }
}
