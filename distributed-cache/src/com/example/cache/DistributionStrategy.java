package com.example.cache;

/**
 * Strategy interface for distributing keys across cache nodes.
 * Pluggable: swap modulo for consistent-hashing, map-based routing, etc.
 */
public interface DistributionStrategy {

    /**
     * Given a key and the total number of nodes, return the
     * index (0-based) of the node that should store this key.
     */
    int getNodeIndex(String key, int totalNodes);
}
