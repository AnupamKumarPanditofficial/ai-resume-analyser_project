// backend/src/main/java/com/resumeanalyzer/exception/AiResponseParseException.java
package com.resumeanalyzer.exception;

/**
 * Thrown when the OpenAI API response cannot be parsed into the expected schema.
 *
 * <p>Possible causes:</p>
 * <ul>
 *   <li>AI returned markdown fences or prose instead of pure JSON.</li>
 *   <li>Required fields ({@code atsScore}, {@code matchedKeywords}, etc.) are missing.</li>
 *   <li>Fields have incorrect types (e.g., {@code atsScore} is a string instead of int).</li>
 *   <li>OpenAI returned an error response after all retries were exhausted.</li>
 * </ul>
 *
 * <p>Carries the raw response body for debugging. Handled by the global exception handler → HTTP 502.</p>
 */
public class AiResponseParseException extends RuntimeException {

    private final String rawResponse;

    /**
     * @param message     human-readable description of the parse failure
     * @param rawResponse the raw response body from OpenAI (for debugging/logging)
     */
    public AiResponseParseException(String message, String rawResponse) {
        super(message);
        this.rawResponse = rawResponse;
    }

    public AiResponseParseException(String message, String rawResponse, Throwable cause) {
        super(message, cause);
        this.rawResponse = rawResponse;
    }

    /**
     * @return the raw response body received from OpenAI (may be truncated in logs)
     */
    public String getRawResponse() {
        return rawResponse;
    }
}
