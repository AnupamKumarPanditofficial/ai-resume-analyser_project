// backend/src/main/java/com/resumeanalyzer/exception/RateLimitExceededException.java
package com.resumeanalyzer.exception;

/**
 * Thrown when a client IP exceeds the configured request rate limit.
 *
 * <p>Primarily used by {@link com.resumeanalyzer.filter.RateLimitingFilter} to signal
 * that the request should be rejected with HTTP 429. The filter handles the response
 * directly (short-circuits before reaching the controller), but this exception is also
 * handled by the global exception handler as a defensive fallback.</p>
 */
public class RateLimitExceededException extends RuntimeException {

    private final long retryAfterSeconds;

    /**
     * @param message           human-readable description of the rate limit violation
     * @param retryAfterSeconds seconds until the client's next token becomes available
     */
    public RateLimitExceededException(String message, long retryAfterSeconds) {
        super(message);
        this.retryAfterSeconds = retryAfterSeconds;
    }

    /**
     * @return seconds the client should wait before retrying
     */
    public long getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
