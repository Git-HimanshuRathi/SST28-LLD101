package com.example.cache;

/**
 * Strategy interface for cache eviction.
 * Pluggable: swap LRU for MRU, LFU, etc. without touching CacheNode.
 */
public interface EvictionPolicy<K> {

    /** Called every time a key is accessed (get or put). */
    void keyAccessed(K key);

    /** Called when a new key is added to the cache. */
    void keyAdded(K key);

    /** Called when a key is removed (evicted or explicitly deleted). */
    void keyRemoved(K key);

    /** Returns the key that should be evicted next. */
    K evict();
}
