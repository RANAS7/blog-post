package com.msp.everestFitness.cache;

import com.msp.everestFitness.model.Orders;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Cache {
    private static final Map<String, Orders> cache = new ConcurrentHashMap<>();

    public static void put(String key, Orders value) {
        cache.put(key, value);
    }

    public static Orders get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }
}
