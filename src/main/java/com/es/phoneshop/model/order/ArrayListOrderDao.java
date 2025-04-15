package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.OrderNotFoundException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.PriceHistory;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.ProductDao;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;

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

public class ArrayListOrderDao implements OrderDao {
    private static volatile OrderDao instance;
    private final List<Order> orderList;
    private long orderId = 1;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    public ArrayListOrderDao() {
        orderList = new ArrayList<>();
    }

    public static OrderDao getInstance() {
        if (instance == null) {
            synchronized (ArrayListOrderDao.class) {
                if (instance == null) {
                    instance = new ArrayListOrderDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Order getOrder(Long id) throws OrderNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return  orderList.stream()
                    .filter((product -> id.equals(product.getId())))
                    .findAny()
                    .orElseThrow();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Order getOrderBySecureId(String secureId) throws OrderNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return  orderList.stream()
                    .filter((product -> secureId.equals(product.getSecureId())))
                    .findAny()
                    .orElseThrow();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Order order) throws OrderNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            Long id = order.getId();
            if (id != null) {
                orderList.remove(getOrder(id));
                orderList.add(order);
            }
            else {
                order.setId(++orderId);
                orderList.add(order);
            }
        } finally {
            readLock.unlock();
        }

    }
}
