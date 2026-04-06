package com.example.ratelimiter;

/**
 * Gateway that guards external resource calls with rate limiting.
 *
 * Internal services call this gateway before making paid external API calls.
 * The gateway consults the rate limiter and only proceeds if within limits.
 */
public class ExternalResourceGateway {

    private final RateLimiter rateLimiter;
    private final ExternalResource externalResource;

    public ExternalResourceGateway(RateLimiter rateLimiter, ExternalResource externalResource) {
        this.rateLimiter = rateLimiter;
        this.externalResource = externalResource;
    }

    /**
     * Attempts to call the external resource on behalf of the given key.
     *
     * @param rateLimitKey the key for rate limiting (customerId, tenantId, etc.)
     * @param request      the request payload to send
     * @return the response from the external resource, or null if rate-limited
     */
    public String call(String rateLimitKey, String request) {
        if (!rateLimiter.allowRequest(rateLimitKey)) {
            System.out.println("[RATE LIMITED] key=\"" + rateLimitKey + "\" — request denied");
            return null;
        }

        System.out.println("[ALLOWED]     key=\"" + rateLimitKey + "\" — calling external resource");
        return externalResource.call(request);
    }
}
