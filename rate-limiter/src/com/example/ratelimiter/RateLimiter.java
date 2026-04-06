package com.example.ratelimiter;

/**
 * Strategy interface for rate limiting.
 * Pluggable: swap Fixed Window for Sliding Window, Token Bucket, etc.
 */
public interface RateLimiter {

    /**
     * Checks whether the request identified by the given key is allowed.
     *
     * @param key the rate-limiting key (e.g. customerId, tenantId, apiKey)
     * @return true if the request is within limits, false if it should be denied
     */
    boolean allowRequest(String key);
}
