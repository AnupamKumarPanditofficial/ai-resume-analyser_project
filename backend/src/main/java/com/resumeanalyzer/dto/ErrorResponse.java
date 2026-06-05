// backend/src/main/java/com/resumeanalyzer/dto/ErrorResponse.java
package com.resumeanalyzer.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Unified error response contract.
 *
 * <p>All API errors (400, 413, 415, 429, 500, 502) return this exact shape:</p>
 * <pre>
 * {
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Job description must be between 50 and 5000 characters.",
 *   "timestamp": "2025-06-05T11:20:01"
 * }
 * </pre>
 *
 * @param status    HTTP status code (e.g., 400, 413, 429)
 * @param error     HTTP status reason phrase (e.g., "Bad Request")
 * @param message   human-readable error description
 * @param timestamp ISO-8601 formatted server timestamp
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String timestamp
) {
    /**
     * Factory method that auto-generates the timestamp.
     *
     * @param status  HTTP status code
     * @param error   HTTP status reason phrase
     * @param message human-readable error description
     * @return a new {@link ErrorResponse} with the current server timestamp
     */
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(
                status,
                error,
                message,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}
