package com.es.phoneshop.model.order;

import com.es.phoneshop.exception.OrderNotFoundException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public Order getOrderBySecureId(String secureId) throws OrderNotFoundException {
        Lock readLock = rwLock.readLock();
        readLock.lock();
        try {
            return orderList.stream()
                    .filter(order -> secureId.equals(order.getSecureId()))
                    .findFirst()
                    .orElseThrow(() -> new OrderNotFoundException(secureId));
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public void save(Order order) {
        Lock writeLock = rwLock.writeLock();
        writeLock.lock();
        try {
            Long id = order.getId();
            if (id != null) {
                orderList.removeIf(o -> o.getId().equals(id));
            } else {
                order.setId(++orderId);
            }
            orderList.add(order);
        } finally {
            writeLock.unlock();
        }
    }
}