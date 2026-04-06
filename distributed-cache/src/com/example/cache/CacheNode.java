package com.example.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * A single cache node with bounded capacity and a pluggable eviction policy.
 *
 * Thread-safety note: each public method is synchronized so the node
 * can be safely accessed from multiple threads.
 */
public class CacheNode {

    private final int id;
    private final int capacity;
    private final Map<String, String> store;
    private final EvictionPolicy<String> evictionPolicy;

    public CacheNode(int id, int capacity, EvictionPolicy<String> evictionPolicy) {
        this.id = id;
        this.capacity = capacity;
        this.store = new HashMap<>();
        this.evictionPolicy = evictionPolicy;
    }

    /** Returns the value mapped to key, or null if absent. Marks the key as accessed. */
    public synchronized String get(String key) {
        String value = store.get(key);
        if (value != null) {
            evictionPolicy.keyAccessed(key);
        }
        return value;
    }

    /** Stores key→value. If at capacity, evicts first. */
    public synchronized void put(String key, String value) {
        if (store.containsKey(key)) {
            // Update existing – no eviction needed
            store.put(key, value);
            evictionPolicy.keyAccessed(key);
            return;
        }

        if (store.size() >= capacity) {
            String evictedKey = evictionPolicy.evict();
            if (evictedKey != null) {
                store.remove(evictedKey);
                System.out.println("  [Node-" + id + "] Evicted key: \"" + evictedKey + "\"");
            }
        }

        store.put(key, value);
        evictionPolicy.keyAdded(key);
    }

    public int getId() {
        return id;
    }

    public synchronized int size() {
        return store.size();
    }

    @Override
    public String toString() {
        return "CacheNode-" + id + " (size=" + store.size() + "/" + capacity + ", keys=" + store.keySet() + ")";
    }
}
