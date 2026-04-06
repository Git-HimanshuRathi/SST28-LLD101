package com.example.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * The main façade that ties together:
 *   • N cache nodes (configurable)
 *   • A pluggable DistributionStrategy (decides which node stores a key)
 *   • A pluggable EvictionPolicy      (decides what to evict when a node is full)
 *   • A Database abstraction           (handles cache misses)
 *
 * Data flow:
 *   get(key)  → strategy picks node → if hit, return → if miss, fetch from DB, cache, return
 *   put(key,v)→ strategy picks node → store in node  → also write-through to DB
 */
public class DistributedCache {

    private final List<CacheNode> nodes;
    private final DistributionStrategy distributionStrategy;
    private final Database database;

    /**
     * @param numberOfNodes        how many cache nodes to create
     * @param capacityPerNode      max entries per node
     * @param distributionStrategy strategy to map key → node index
     * @param evictionSupplier     factory that creates a fresh EvictionPolicy per node
     * @param database             backing data store
     */
    public DistributedCache(int numberOfNodes,
                            int capacityPerNode,
                            DistributionStrategy distributionStrategy,
                            Supplier<EvictionPolicy<String>> evictionSupplier,
                            Database database) {
        this.distributionStrategy = distributionStrategy;
        this.database = database;
        this.nodes = new ArrayList<>();

        for (int i = 0; i < numberOfNodes; i++) {
            nodes.add(new CacheNode(i, capacityPerNode, evictionSupplier.get()));
        }
    }

    // ─── Public API ───────────────────────────────────────────

    /**
     * Retrieves the value for the given key.
     *   1. Determine the owning node via the distribution strategy.
     *   2. If the node has the key → cache hit → return value.
     *   3. Otherwise → cache miss → fetch from DB, store in node, return.
     */
    public String get(String key) {
        int nodeIndex = distributionStrategy.getNodeIndex(key, nodes.size());
        CacheNode node = nodes.get(nodeIndex);

        String value = node.get(key);
        if (value != null) {
            System.out.println("[CACHE HIT]  key=\"" + key + "\" → Node-" + nodeIndex);
            return value;
        }

        // Cache miss – fetch from DB
        System.out.println("[CACHE MISS] key=\"" + key + "\" → fetching from DB");
        value = database.get(key);
        if (value != null) {
            node.put(key, value);  // warm the cache
        }
        return value;
    }

    /**
     * Stores a key-value pair.
     *   1. Determine the owning node via the distribution strategy.
     *   2. Store in that node (eviction happens inside CacheNode if full).
     *   3. Write-through to the database.
     */
    public void put(String key, String value) {
        int nodeIndex = distributionStrategy.getNodeIndex(key, nodes.size());
        CacheNode node = nodes.get(nodeIndex);
        node.put(key, value);
        database.put(key, value);   // write-through
        System.out.println("[PUT]        key=\"" + key + "\" → Node-" + nodeIndex);
    }

    // ─── Diagnostics ──────────────────────────────────────────

    public void printStatus() {
        System.out.println("\n--- Cache Status ---");
        for (CacheNode node : nodes) {
            System.out.println("  " + node);
        }
        System.out.println("--------------------\n");
    }
}
