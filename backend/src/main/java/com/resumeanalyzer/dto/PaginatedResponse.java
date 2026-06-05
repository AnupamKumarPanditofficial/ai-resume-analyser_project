// backend/src/main/java/com/resumeanalyzer/dto/PaginatedResponse.java
package com.resumeanalyzer.dto;

import java.util.List;

/**
 * Generic paginated response wrapper for list endpoints.
 *
 * <p>Used by {@code GET /api/v1/analyses} to return paginated analysis records.</p>
 *
 * @param content       the list of items for the current page
 * @param page          current page number (0-indexed)
 * @param size          page size (number of items per page)
 * @param totalElements total number of items across all pages
 * @param totalPages    total number of pages
 * @param <T>           the type of items in the page (e.g., {@link AnalysisResponse})
 */
public record PaginatedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
