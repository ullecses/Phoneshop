package com.es.phoneshop.security;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultDosProtectionService implements DosProtectionService {
    private static final long THRESHOLD = 100;
    private static final long WINDOW_TIME = 60000;
    private final Map<String, RequestInfo> countMap = new ConcurrentHashMap<>();
    private static volatile DefaultDosProtectionService instance;

    public static DefaultDosProtectionService getInstance() {
        if (instance == null) {
            synchronized (DefaultDosProtectionService.class) {
                if (instance == null) {
                    instance = new DefaultDosProtectionService();
                }
            }
        }
        return instance;
    }

    @Override
    public boolean isAllowed(String ip) {
        long currentTime = System.currentTimeMillis();
        RequestInfo requestInfo = countMap.get(ip);

        if (requestInfo == null) {
            requestInfo = new RequestInfo(new AtomicLong(1), currentTime); // Первый запрос от этого IP
            countMap.put(ip, requestInfo);
            return true;
        }

        if (currentTime - requestInfo.lastRequestTime > WINDOW_TIME) {
            requestInfo.count.set(1);
            requestInfo.lastRequestTime = currentTime;
        } else {
            return requestInfo.count.incrementAndGet() <= THRESHOLD;
        }

        return true;
    }

    private static class RequestInfo {
        AtomicLong count;
        long lastRequestTime;

        public RequestInfo(AtomicLong count, long lastRequestTime) {
            this.count = count;
            this.lastRequestTime = lastRequestTime;
        }
    }
}
