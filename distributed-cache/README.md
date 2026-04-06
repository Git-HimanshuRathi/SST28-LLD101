# Distributed Cache

## Requirements

1. **APIs:**
   - `get(key)` → return cached value, or fetch from DB on cache miss
   - `put(key, value)` → store in cache node + write-through to DB
2. **Distributed:** Cache is split across N configurable nodes
3. **Pluggable Distribution Strategy:** modulo-based (current), consistent hashing (future)
4. **Pluggable Eviction Policy:** LRU (current), MRU / LFU (future)
5. **Cache Miss Handling:** fetch from DB → store in cache → return
6. **Bounded Capacity:** each node has a configurable max size; eviction kicks in when full
7. **Thread-Safety:** each cache node is synchronized

## Class Diagram

```
+----------------------------+
|    <<interface>>           |
|    Database                |
|----------------------------|
| + get(key): String         |
| + put(key, value): void    |
+----------------------------+
         ^
         |
+----------------------------+
|   InMemoryDatabase         |
|----------------------------|
| - store: HashMap           |
|----------------------------|
| + get(key)                 |
| + put(key, value)          |
| + seed(key, value)         |
+----------------------------+

+----------------------------+       +----------------------------+
| <<interface>>              |       | <<interface>>              |
| DistributionStrategy       |       | EvictionPolicy<K>          |
|----------------------------|       |----------------------------|
| + getNodeIndex(key,        |       | + keyAccessed(key)         |
|     totalNodes): int       |       | + keyAdded(key)            |
+----------------------------+       | + keyRemoved(key)          |
         ^                           | + evict(): K               |
         |                           +----------------------------+
+----------------------------+                ^
| ModuloDistributionStrategy |                |
|----------------------------|       +----------------------------+
| hash(key) % totalNodes     |       | LRUEvictionPolicy<K>       |
+----------------------------+       |----------------------------|
                                     | - accessOrder:             |
                                     |     LinkedHashMap (access  |
                                     |     order mode)            |
                                     |----------------------------|
                                     | head = LRU, tail = MRU     |
                                     | + evict() → remove head    |
                                     +----------------------------+

+-------------------------------+
|         CacheNode             |       (Thread-safe: synchronized methods)
|-------------------------------|
| - id: int                     |
| - capacity: int               |
| - store: HashMap<String,String>|
| - evictionPolicy: EvictionPolicy|
|-------------------------------|
| + get(key): String            |  ← marks key as accessed
| + put(key, value): void       |  ← evicts if at capacity
| + size(): int                 |
+-------------------------------+

+-------------------------------+
|      DistributedCache         |       (Main facade)
|-------------------------------|
| - nodes: List<CacheNode>     |
| - distributionStrategy        |
| - database: Database          |
|-------------------------------|
| + get(key): String            |  ← strategy → node → hit/miss → DB fallback
| + put(key, value): void       |  ← strategy → node → store + write-through
| + printStatus(): void         |
+-------------------------------+
```

## Data Flow

### get(key)
```
Client → DistributedCache.get(key)
       → DistributionStrategy.getNodeIndex(key, N) → nodeIndex
       → CacheNode[nodeIndex].get(key)
       → if HIT:  return value
       → if MISS: Database.get(key) → CacheNode.put(key, value) → return value
```

### put(key, value)
```
Client → DistributedCache.put(key, value)
       → DistributionStrategy.getNodeIndex(key, N) → nodeIndex
       → CacheNode[nodeIndex].put(key, value)   ← evicts LRU if full
       → Database.put(key, value)                ← write-through
```

### Eviction (inside CacheNode.put)
```
if store.size >= capacity:
    evictedKey = EvictionPolicy.evict()   ← LRU: removes head of LinkedHashMap
    store.remove(evictedKey)
store.put(key, value)
EvictionPolicy.keyAdded(key)
```

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `DistributionStrategy` | Pluggable key-to-node mapping (modulo now, consistent hashing later) |
| **Strategy** | `EvictionPolicy<K>` | Pluggable eviction (LRU now, MRU/LFU later) |
| **Facade** | `DistributedCache` | Single entry point that hides node routing + DB fallback |
| **Factory Method** | `Supplier<EvictionPolicy>` in constructor | Each node gets its own independent eviction policy instance |

## Extensibility

| Extension | How |
|-----------|-----|
| Add Consistent Hashing | Implement `DistributionStrategy` → pass to `DistributedCache` |
| Add MRU / LFU eviction | Implement `EvictionPolicy<K>` → pass supplier to constructor |
| Add TTL-based expiry | Extend `CacheNode` to track timestamps |
| Add replication | Add replica nodes in `DistributedCache` |

## Build/Run

```
cd distributed-cache/src
javac com/example/cache/*.java
java com.example.cache.App
```
