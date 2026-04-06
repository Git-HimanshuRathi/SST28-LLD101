package com.example.cache;

/**
 * Abstraction for the backing data store.
 * In a real system this would be a DB client; here we use an
 * in-memory HashMap as stub.
 */
public interface Database {

    /** Fetch a value from the persistent store. Returns null if absent. */
    String get(String key);

    /** Write a value to the persistent store. */
    void put(String key, String value);
}
