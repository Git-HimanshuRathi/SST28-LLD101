package com.example.ratelimiter;

/**
 * Factory to create RateLimiter instances by algorithm name.
 * Callers can switch algorithms via configuration without changing business logic.
 */
public class RateLimiterFactory {

    public enum Algorithm {
        FIXED_WINDOW,
        SLIDING_WINDOW
    }

    public static RateLimiter create(Algorithm algorithm, RateLimiterConfig config) {
        switch (algorithm) {
            case FIXED_WINDOW:
                return new FixedWindowRateLimiter(config);
            case SLIDING_WINDOW:
                return new SlidingWindowRateLimiter(config);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }
}
