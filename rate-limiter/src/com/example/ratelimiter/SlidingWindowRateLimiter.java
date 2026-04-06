package com.example.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Sliding Window Counter rate limiter.
 *
 * Maintains a deque of timestamps for each key. On each request:
 *   1. Remove all timestamps older than (now - windowSize).
 *   2. If remaining count < maxRequests → allow and record timestamp.
 *   3. Otherwise → deny.
 *
 * Trade-offs:
 *   + No boundary burst problem — the window slides smoothly.
 *   + More accurate than Fixed Window.
 *   - Higher memory: stores one timestamp per request (up to maxRequests per key).
 *   - Slightly more CPU: cleanup of expired entries on each call.
 *
 * Thread-safety: uses ConcurrentHashMap + synchronized per-key block.
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    private final RateLimiterConfig config;
    private final ConcurrentHashMap<String, ConcurrentLinkedDeque<Long>> requestLogs;

    public SlidingWindowRateLimiter(RateLimiterConfig config) {
        this.config = config;
        this.requestLogs = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long windowStart = now - config.getWindowSizeMillis();

        ConcurrentLinkedDeque<Long> log = requestLogs.computeIfAbsent(
                key, k -> new ConcurrentLinkedDeque<>()
        );

        synchronized (log) {
            // Evict expired timestamps
            while (!log.isEmpty() && log.peekFirst() <= windowStart) {
                log.pollFirst();
            }

            if (log.size() < config.getMaxRequests()) {
                log.addLast(now);
                return true;
            }

            return false;
        }
    }
}
