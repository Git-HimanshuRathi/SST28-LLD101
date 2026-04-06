package com.example.ratelimiter;

import java.util.concurrent.TimeUnit;

/**
 * Simulation driver demonstrating the rate limiting system.
 *
 * Scenarios covered:
 *   1. Fixed Window  — tenant T1 allowed 5 calls/minute
 *   2. Sliding Window — same config, smoother limiting
 *   3. Switching algorithms without changing business logic
 *   4. Multiple keys (per-tenant isolation)
 */
public class App {

    public static void main(String[] args) throws InterruptedException {

        RateLimiterConfig config = new RateLimiterConfig(5, 1, TimeUnit.MINUTES);
        ExternalResource resource = new StubExternalResource();

        System.out.println("════════════════════════════════════════════════════════");
        System.out.println(" Rate Limiter Demo  (5 requests per minute)");
        System.out.println("════════════════════════════════════════════════════════\n");

        // ─── Demo 1: Fixed Window Counter ─────────────────────
        System.out.println("▸ Demo 1: Fixed Window Counter");
        System.out.println("─────────────────────────────────────────────────\n");

        RateLimiter fixedWindow = RateLimiterFactory.create(
                RateLimiterFactory.Algorithm.FIXED_WINDOW, config
        );
        ExternalResourceGateway gateway1 = new ExternalResourceGateway(fixedWindow, resource);

        for (int i = 1; i <= 8; i++) {
            String result = gateway1.call("tenant:T1", "request-" + i);
            System.out.println("  Request " + i + " → " + (result != null ? result : "DENIED") + "\n");
        }

        // ─── Demo 2: Sliding Window Counter ──────────────────
        System.out.println("▸ Demo 2: Sliding Window Counter");
        System.out.println("─────────────────────────────────────────────────\n");

        RateLimiter slidingWindow = RateLimiterFactory.create(
                RateLimiterFactory.Algorithm.SLIDING_WINDOW, config
        );
        ExternalResourceGateway gateway2 = new ExternalResourceGateway(slidingWindow, resource);

        for (int i = 1; i <= 8; i++) {
            String result = gateway2.call("tenant:T1", "request-" + i);
            System.out.println("  Request " + i + " → " + (result != null ? result : "DENIED") + "\n");
        }

        // ─── Demo 3: Per-key isolation ────────────────────────
        System.out.println("▸ Demo 3: Per-tenant isolation (Sliding Window)");
        System.out.println("─────────────────────────────────────────────────\n");

        RateLimiter multiTenant = RateLimiterFactory.create(
                RateLimiterFactory.Algorithm.SLIDING_WINDOW, config
        );
        ExternalResourceGateway gateway3 = new ExternalResourceGateway(multiTenant, resource);

        System.out.println("  Tenant T2: 5 requests (all should pass)");
        for (int i = 1; i <= 5; i++) {
            gateway3.call("tenant:T2", "t2-req-" + i);
        }

        System.out.println("\n  Tenant T3: 5 requests (all should pass — independent quota)");
        for (int i = 1; i <= 5; i++) {
            gateway3.call("tenant:T3", "t3-req-" + i);
        }

        System.out.println("\n  Tenant T2: 1 more request (should be DENIED — over limit)");
        String result = gateway3.call("tenant:T2", "t2-req-6");
        System.out.println("  → " + (result != null ? result : "DENIED"));

        // ─── Demo 4: Switching algorithm at runtime ───────────
        System.out.println("\n\n▸ Demo 4: Switch algorithm without changing business logic");
        System.out.println("─────────────────────────────────────────────────\n");

        // Same gateway pattern, just pass a different RateLimiter
        RateLimiter tokenBucketPlaceholder = RateLimiterFactory.create(
                RateLimiterFactory.Algorithm.FIXED_WINDOW, config
        );
        ExternalResourceGateway gateway4 = new ExternalResourceGateway(tokenBucketPlaceholder, resource);
        System.out.println("  (Switched to Fixed Window — business logic unchanged)");
        gateway4.call("tenant:T4", "switched-request");

        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println(" Demo complete.");
        System.out.println("════════════════════════════════════════════════════════");
    }
}
