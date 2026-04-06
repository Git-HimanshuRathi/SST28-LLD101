package com.example.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * Stub database backed by a simple HashMap.
 * Pre-populated with sample data for demonstration.
 */
public class InMemoryDatabase implements Database {

    private final Map<String, String> store = new HashMap<>();

    @Override
    public String get(String key) {
        return store.get(key);
    }

    @Override
    public void put(String key, String value) {
        store.put(key, value);
    }

    /** Helper to pre-load data for testing. */
    public void seed(String key, String value) {
        store.put(key, value);
    }
}
