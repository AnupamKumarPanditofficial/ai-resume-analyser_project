// backend/src/main/java/com/resumeanalyzer/exception/GlobalExceptionHandler.java
package com.resumeanalyzer.exception;

import com.resumeanalyzer.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

/**
 * Centralized exception handler for all REST controllers.
 *
 * <p>Returns the unified {@link ErrorResponse} contract for every error type.
 * Each handler maps a specific exception to the appropriate HTTP status code.</p>
 *
 * <h3>Exception → HTTP Status Mapping</h3>
 * <table>
 *   <tr><th>Exception</th><th>HTTP Status</th></tr>
 *   <tr><td>{@link ConstraintViolationException}</td><td>400 Bad Request</td></tr>
 *   <tr><td>{@link MethodArgumentNotValidException}</td><td>400 Bad Request</td></tr>
 *   <tr><td>{@link FileTooLargeException}</td><td>413 Payload Too Large</td></tr>
 *   <tr><td>{@link MaxUploadSizeExceededException}</td><td>413 Payload Too Large</td></tr>
 *   <tr><td>{@link UnsupportedMediaTypeException}</td><td>415 Unsupported Media Type</td></tr>
 *   <tr><td>{@link RateLimitExceededException}</td><td>429 Too Many Requests</td></tr>
 *   <tr><td>{@link AiResponseParseException}</td><td>502 Bad Gateway</td></tr>
 *   <tr><td>{@link Exception} (fallback)</td><td>500 Internal Server Error</td></tr>
 * </table>
 *
 * <h3>Important: ConstraintViolationException vs MethodArgumentNotValidException</h3>
 * <ul>
 *   <li>{@code @Valid} on a {@code @RequestParam String} triggers
 *       {@link ConstraintViolationException} (from Jakarta Validation).</li>
 *   <li>{@code @Valid} on a {@code @RequestBody} POJO triggers
 *       {@link MethodArgumentNotValidException} (Spring wraps it).</li>
 *   <li>In our API, only {@code ConstraintViolationException} fires since
 *       {@code jobDescription} is a {@code @RequestParam}. The MANVE handler is
 *       included as a defensive measure for future endpoints.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles constraint violations from {@code @Valid} on {@code @RequestParam} fields.
     * This is the primary validation error handler for our API.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("Constraint violation: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message));
    }

    /**
     * Handles validation errors from {@code @Valid} on {@code @RequestBody} POJOs.
     * Defensive handler — not currently triggered by our API but included for future safety.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.warn("Validation error: {}", message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message));
    }

    /**
     * Handles our custom file-too-large exception (from PdfExtractionService size check).
     */
    @ExceptionHandler(FileTooLargeException.class)
    public ResponseEntity<ErrorResponse> handleFileTooLarge(FileTooLargeException ex) {
        log.warn("File too large: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ErrorResponse.of(413, "Payload Too Large", ex.getMessage()));
    }

    /**
     * Handles Spring's built-in multipart size limit exceeded exception.
     * Triggered by {@code spring.servlet.multipart.max-file-size} / {@code max-request-size}.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.warn("Max upload size exceeded: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ErrorResponse.of(413, "Payload Too Large",
                        "File size exceeds the maximum allowed upload size of 5 MB."));
    }

    /**
     * Handles non-PDF file uploads (custom exception, not Spring's content negotiation exception).
     */
    @ExceptionHandler(UnsupportedMediaTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(
            UnsupportedMediaTypeException ex) {

        log.warn("Unsupported media type: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ErrorResponse.of(415, "Unsupported Media Type", ex.getMessage()));
    }

    /**
     * Handles rate limit violations.
     * Defensive — the filter normally short-circuits before reaching the controller,
     * but this catches any edge case where the exception propagates.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(RateLimitExceededException ex) {
        log.warn("Rate limit exceeded (handler): {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(ErrorResponse.of(429, "Too Many Requests", ex.getMessage()));
    }

    /**
     * Handles malformed or incomplete AI responses.
     * Mapped to 502 Bad Gateway — the upstream AI service returned an unusable response.
     */
    @ExceptionHandler(AiResponseParseException.class)
    public ResponseEntity<ErrorResponse> handleAiResponseParseError(AiResponseParseException ex) {
        log.error("AI response parse error: {}. Raw response: {}",
                ex.getMessage(), truncate(ex.getRawResponse(), 500));

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.of(502, "Bad Gateway",
                        "The AI service returned an invalid response. Please try again."));
    }

    /**
     * Handles PDF extraction failures.
     */
    @ExceptionHandler(PdfExtractionException.class)
    public ResponseEntity<ErrorResponse> handlePdfExtraction(PdfExtractionException ex) {
        log.error("PDF extraction failed: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error",
                        "Failed to extract text from the uploaded PDF. " + ex.getMessage()));
    }

    /**
     * Fallback handler for any unexpected exception.
     * Logs the full stack trace and returns a generic 500 error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error",
                        "An unexpected error occurred. Please try again later."));
    }

    /**
     * Truncates a string for safe logging.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "null";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength) + "...";
    }
}
