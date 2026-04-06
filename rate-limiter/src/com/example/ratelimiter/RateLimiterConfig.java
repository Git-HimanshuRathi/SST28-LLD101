package com.example.ratelimiter;

import java.util.concurrent.TimeUnit;

/**
 * Configuration for a rate limiter: max requests allowed within a time window.
 * Immutable value object.
 */
public class RateLimiterConfig {

    private final int maxRequests;
    private final long windowSizeMillis;

    public RateLimiterConfig(int maxRequests, long windowSize, TimeUnit unit) {
        if (maxRequests <= 0) throw new IllegalArgumentException("maxRequests must be > 0");
        if (windowSize <= 0) throw new IllegalArgumentException("windowSize must be > 0");
        this.maxRequests = maxRequests;
        this.windowSizeMillis = unit.toMillis(windowSize);
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    public long getWindowSizeMillis() {
        return windowSizeMillis;
    }

    @Override
    public String toString() {
        return maxRequests + " requests per " + windowSizeMillis + "ms";
    }
}
