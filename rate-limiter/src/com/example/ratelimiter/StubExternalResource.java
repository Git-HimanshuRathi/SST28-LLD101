package com.example.ratelimiter;

/**
 * Stub implementation of an external resource for demonstration.
 * Simulates a paid API that returns a processed result.
 */
public class StubExternalResource implements ExternalResource {

    @Override
    public String call(String request) {
        return "ExternalResponse[" + request + "]";
    }
}
