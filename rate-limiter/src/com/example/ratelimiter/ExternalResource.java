package com.example.ratelimiter;

/**
 * Abstraction for a paid external resource/API.
 * In a real system this would be an HTTP client; here we use a stub.
 */
public interface ExternalResource {

    /**
     * Makes a call to the external resource.
     *
     * @param request the request payload
     * @return the response from the external resource
     */
    String call(String request);
}
