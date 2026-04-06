package com.example.ratelimiter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Fixed Window Counter rate limiter.
 *
 * Divides time into fixed-size windows. Each window has a counter.
 * When a request arrives, if the current window's counter < maxRequests → allow.
 * When the window expires, a new window starts with counter = 0.
 *
 * Trade-offs:
 *   + Simple and memory-efficient (one counter per key).
 *   + O(1) per request.
 *   - Burst problem: up to 2x the limit can pass at window boundaries
 *     (e.g. all requests at end of window 1 + start of window 2).
 *
 * Thread-safety: uses ConcurrentHashMap + AtomicReference with CAS loop.
 */
public class FixedWindowRateLimiter implements RateLimiter {

    private final RateLimiterConfig config;
    private final ConcurrentHashMap<String, AtomicReference<WindowState>> windows;

    public FixedWindowRateLimiter(RateLimiterConfig config) {
        this.config = config;
        this.windows = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(String key) {
        long now = System.currentTimeMillis();
        long windowStart = getWindowStart(now);

        AtomicReference<WindowState> ref = windows.computeIfAbsent(
                key, k -> new AtomicReference<>(new WindowState(windowStart, 0))
        );

        while (true) {
            WindowState current = ref.get();
            WindowState next;

            if (current.windowStart != windowStart) {
                // Window expired → reset counter
                next = new WindowState(windowStart, 1);
            } else if (current.count < config.getMaxRequests()) {
                // Within window and under limit → increment
                next = new WindowState(windowStart, current.count + 1);
            } else {
                // Over limit
                return false;
            }

            if (ref.compareAndSet(current, next)) {
                return true;
            }
            // CAS failed → retry
        }
    }

    private long getWindowStart(long timestampMillis) {
        return timestampMillis - (timestampMillis % config.getWindowSizeMillis());
    }

    /** Immutable state for a single window. */
    private static class WindowState {
        final long windowStart;
        final int count;

        WindowState(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
