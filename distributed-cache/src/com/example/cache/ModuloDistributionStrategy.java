package com.example.cache;

/**
 * Simple modulo-based distribution: hash(key) % numberOfNodes.
 *
 * Pros:  Very fast, uniform for good hash functions.
 * Cons:  Adding/removing nodes remaps most keys (not suitable for
 *        dynamic cluster resizing without consistent hashing).
 */
public class ModuloDistributionStrategy implements DistributionStrategy {

    @Override
    public int getNodeIndex(String key, int totalNodes) {
        if (totalNodes <= 0) {
            throw new IllegalArgumentException("totalNodes must be > 0");
        }
        int hash = Math.abs(key.hashCode());
        return hash % totalNodes;
    }
}
