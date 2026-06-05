// backend/src/main/java/com/resumeanalyzer/exception/FileTooLargeException.java
package com.resumeanalyzer.exception;

/**
 * Thrown when an uploaded file exceeds the maximum allowed size (5 MB).
 *
 * <p>Handled by the global exception handler → HTTP 413 (Payload Too Large).</p>
 */
public class FileTooLargeException extends RuntimeException {

    /**
     * @param actualSizeBytes the actual file size in bytes
     * @param maxSizeBytes    the maximum allowed file size in bytes
     */
    public FileTooLargeException(long actualSizeBytes, long maxSizeBytes) {
        super(String.format(
                "File size %s exceeds the maximum allowed size of %s.",
                formatBytes(actualSizeBytes),
                formatBytes(maxSizeBytes)
        ));
    }

    /**
     * Formats a byte count into a human-readable string (e.g., "4.2 MB").
     */
    private static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        double kb = bytes / 1024.0;
        if (kb < 1024) {
            return String.format("%.1f KB", kb);
        }
        double mb = kb / 1024.0;
        return String.format("%.1f MB", mb);
    }
}
