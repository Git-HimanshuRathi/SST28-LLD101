package com.example.cache;

/**
 * Simulation driver that demonstrates the distributed cache system.
 *
 * Scenarios covered:
 *   1. Cache miss → DB fetch → value cached
 *   2. Cache hit  → value returned from cache
 *   3. Eviction   → LRU key evicted when node capacity exceeded
 *   4. Put        → write-through to cache + DB
 */
public class App {

    public static void main(String[] args) {

        // ─── 1. Set up a stub database with seed data ─────────
        InMemoryDatabase db = new InMemoryDatabase();
        db.seed("user:1",  "Alice");
        db.seed("user:2",  "Bob");
        db.seed("user:3",  "Charlie");
        db.seed("user:4",  "Diana");
        db.seed("user:5",  "Eve");
        db.seed("user:6",  "Frank");
        db.seed("user:7",  "Grace");
        db.seed("user:8",  "Heidi");
        db.seed("user:9",  "Ivan");
        db.seed("user:10", "Judy");

        // ─── 2. Create distributed cache ──────────────────────
        //   • 3 nodes, capacity 2 each  (small to show eviction)
        //   • Modulo-based distribution
        //   • LRU eviction
        DistributedCache cache = new DistributedCache(
                3,                                    // number of nodes
                2,                                    // capacity per node
                new ModuloDistributionStrategy(),     // distribution strategy
                LRUEvictionPolicy::new,               // eviction policy factory
                db                                    // backing database
        );

        System.out.println("════════════════════════════════════════════════════");
        System.out.println(" Distributed Cache Demo  (3 nodes, capacity 2, LRU)");
        System.out.println("════════════════════════════════════════════════════\n");

        // ─── 3. Demonstrate Cache Miss → DB Fetch ─────────────
        System.out.println("▸ Step 1: get(\"user:1\") — first access (cache miss)");
        String v1 = cache.get("user:1");
        System.out.println("  Returned: " + v1 + "\n");

        // ─── 4. Demonstrate Cache Hit ─────────────────────────
        System.out.println("▸ Step 2: get(\"user:1\") — second access (cache hit)");
        String v2 = cache.get("user:1");
        System.out.println("  Returned: " + v2 + "\n");

        // ─── 5. Fill nodes & trigger eviction ─────────────────
        System.out.println("▸ Step 3: Fill up nodes to trigger LRU eviction...");
        cache.get("user:2");
        cache.get("user:3");
        cache.get("user:4");
        cache.get("user:5");
        cache.get("user:6");
        cache.get("user:7");
        cache.printStatus();

        System.out.println("▸ Step 4: Access more keys — observe eviction");
        cache.get("user:8");
        cache.get("user:9");
        cache.get("user:10");
        cache.printStatus();

        // ─── 6. Demonstrate put() ─────────────────────────────
        System.out.println("▸ Step 5: put(\"session:abc\", \"token-xyz\")");
        cache.put("session:abc", "token-xyz");
        cache.printStatus();

        // ─── 7. Read it back ──────────────────────────────────
        System.out.println("▸ Step 6: get(\"session:abc\") — should be a cache hit");
        String v3 = cache.get("session:abc");
        System.out.println("  Returned: " + v3 + "\n");

        System.out.println("════════════════════════════════════════════════════");
        System.out.println(" Demo complete.");
        System.out.println("════════════════════════════════════════════════════");
    }
}
