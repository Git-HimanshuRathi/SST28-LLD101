package com.example.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * Least-Recently-Used eviction policy.
 *
 * Uses a LinkedHashMap in access-order mode so the first entry
 * is always the least-recently-used key.
 */
public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    // Access-ordered map: head = LRU, tail = MRU
    private final LinkedHashMap<K, Boolean> accessOrder;

    public LRUEvictionPolicy() {
        // accessOrder = true → entries are in last-access order
        this.accessOrder = new LinkedHashMap<>(16, 0.75f, true);
    }

    @Override
    public void keyAccessed(K key) {
        // Touch the key – LinkedHashMap moves it to tail automatically
        accessOrder.get(key);
    }

    @Override
    public void keyAdded(K key) {
        accessOrder.put(key, Boolean.TRUE);
    }

    @Override
    public void keyRemoved(K key) {
        accessOrder.remove(key);
    }

    @Override
    public K evict() {
        Iterator<Map.Entry<K, Boolean>> it = accessOrder.entrySet().iterator();
        if (!it.hasNext()) {
            return null;
        }
        K lruKey = it.next().getKey();
        it.remove();
        return lruKey;
    }
}
