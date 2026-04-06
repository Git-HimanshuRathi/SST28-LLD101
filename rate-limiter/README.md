# Rate Limiter

## Requirements

1. **Purpose:** Rate limit calls to a paid external resource (not the incoming API)
2. **APIs:**
   - `allowRequest(key)` → `true` if within limit, `false` if denied
   - `ExternalResourceGateway.call(key, request)` → consults rate limiter before calling external API
3. **Pluggable Algorithms:** Fixed Window Counter (current), Sliding Window Counter (current), Token Bucket / Leaky Bucket (future)
4. **Configurable Limits:** e.g. 100 requests/minute, 1000 requests/hour
5. **Flexible Rate-Limit Key:** per customer, per tenant, per API key, per provider
6. **Thread-Safe:** concurrent access from multiple service threads
7. **Applied only at external call point:** not every API request consumes quota

## Class Diagram

```
+----------------------------+
|    <<interface>>           |
|    RateLimiter             |
|----------------------------|
| + allowRequest(key): bool  |
+----------------------------+
         ^
         |
    +----+--------------------+
    |                         |
+----------------------------+  +----------------------------+
| FixedWindowRateLimiter     |  | SlidingWindowRateLimiter   |
|----------------------------|  |----------------------------|
| - config: RateLimiterConfig|  | - config: RateLimiterConfig|
| - windows: ConcurrentMap   |  | - requestLogs: ConcurrentMap|
|   <String, AtomicRef       |  |   <String, Deque<Long>>   |
|     <WindowState>>         |  |----------------------------|
|----------------------------|  | + allowRequest(key)        |
| + allowRequest(key)        |  |   ← evict expired,        |
|   ← CAS loop, lock-free   |  |     count remaining        |
+----------------------------+  +----------------------------+

+----------------------------+
|   RateLimiterConfig        |       (Immutable value object)
|----------------------------|
| - maxRequests: int         |
| - windowSizeMillis: long   |
|----------------------------|
| + getMaxRequests()         |
| + getWindowSizeMillis()    |
+----------------------------+

+----------------------------+
|   RateLimiterFactory       |       (Switch algorithm without changing business logic)
|----------------------------|
| <<enum>> Algorithm         |
|   FIXED_WINDOW             |
|   SLIDING_WINDOW           |
|----------------------------|
| + create(algorithm, config)|
|     : RateLimiter          |
+----------------------------+

+----------------------------+
|    <<interface>>           |
|    ExternalResource        |
|----------------------------|
| + call(request): String    |
+----------------------------+
         ^
         |
+----------------------------+
|   StubExternalResource     |
|----------------------------|
| + call(request): String    |
|   ← returns stub response  |
+----------------------------+

+-------------------------------+
|   ExternalResourceGateway     |       (Guards external calls with rate limiting)
|-------------------------------|
| - rateLimiter: RateLimiter    |
| - externalResource            |
|-------------------------------|
| + call(rateLimitKey, request) |
|   ← if allowed: call external|
|   ← if denied: return null   |
+-------------------------------+
```

## Data Flow

### Request Flow (Internal Service → External Resource)
```
InternalService → needs external call?
   NO  → proceed normally (no rate limiting)
   YES → ExternalResourceGateway.call(tenantId, request)
       → RateLimiter.allowRequest(tenantId)
       → if ALLOWED: ExternalResource.call(request) → return response
       → if DENIED:  return null (or throw, or queue)
```

### Fixed Window Algorithm
```
Time divided into fixed windows: [0-60s], [60-120s], ...

allowRequest(key):
    windowStart = now - (now % windowSize)
    if current window expired → reset counter to 1, ALLOW
    if counter < maxRequests  → increment, ALLOW
    else                      → DENY
```

### Sliding Window Algorithm
```
Maintains deque of timestamps per key.

allowRequest(key):
    evict all timestamps older than (now - windowSize)
    if remaining count < maxRequests → add now, ALLOW
    else                             → DENY
```

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `RateLimiter` interface | Pluggable rate limiting algorithms |
| **Factory** | `RateLimiterFactory` | Create limiter by enum — swap without changing caller code |
| **Gateway** | `ExternalResourceGateway` | Single chokepoint guarding external resource access |

## Algorithm Trade-offs

| | Fixed Window | Sliding Window |
|---|---|---|
| **Memory** | O(1) per key | O(maxRequests) per key |
| **Accuracy** | Boundary burst: up to 2x limit at window edges | Smooth — no burst problem |
| **Speed** | O(1) constant | O(expired entries) amortized |
| **Thread-safety** | Lock-free CAS | Synchronized per-key |
| **Simplicity** | Very simple | Moderate |

## Extensibility

| Extension | How |
|-----------|-----|
| Add Token Bucket | Implement `RateLimiter` + add to `RateLimiterFactory.Algorithm` |
| Add Leaky Bucket | Implement `RateLimiter` + add to factory |
| Add Sliding Log | Implement `RateLimiter` with sorted timestamp log |
| Composite limits | Chain multiple `RateLimiter` instances (e.g. 100/min AND 1000/hr) |
| Distributed rate limiting | Back `RateLimiter` with Redis instead of in-memory maps |

## Build/Run

```
cd rate-limiter/src
javac com/example/ratelimiter/*.java
java com.example.ratelimiter.App
```
