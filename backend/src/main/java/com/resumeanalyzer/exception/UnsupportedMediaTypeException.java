// backend/src/main/java/com/resumeanalyzer/exception/UnsupportedMediaTypeException.java
package com.resumeanalyzer.exception;

/**
 * Thrown when an uploaded file has a MIME type other than {@code application/pdf}.
 *
 * <p>This is a <b>custom</b> exception — not Spring's built-in
 * {@code HttpMediaTypeNotSupportedException}, which handles content negotiation
 * (e.g., wrong {@code Content-Type} header on the request body) and is not suitable
 * for per-file MIME validation in multipart uploads.</p>
 *
 * <p>Handled by the global exception handler → HTTP 415 (Unsupported Media Type).</p>
 */
public class UnsupportedMediaTypeException extends RuntimeException {

    public UnsupportedMediaTypeException(String message) {
        super(message);
    }
}
