// backend/src/main/java/com/resumeanalyzer/exception/PdfExtractionException.java
package com.resumeanalyzer.exception;

/**
 * Thrown when PDF text extraction fails for both PDFBox (primary) and Tika (fallback).
 *
 * <p>Wraps the underlying {@link java.io.IOException} or parsing error.
 * Handled by the global exception handler → HTTP 500.</p>
 */
public class PdfExtractionException extends RuntimeException {

    public PdfExtractionException(String message) {
        super(message);
    }

    public PdfExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
