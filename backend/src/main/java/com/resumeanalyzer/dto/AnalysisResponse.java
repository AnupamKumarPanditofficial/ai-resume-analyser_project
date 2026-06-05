// backend/src/main/java/com/resumeanalyzer/dto/AnalysisResponse.java
package com.resumeanalyzer.dto;

import com.resumeanalyzer.model.BulletImprovement;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO returned to the frontend after a successful analysis.
 *
 * <p>Maps from {@link com.resumeanalyzer.model.ResumeAnalysis} document or
 * directly from {@link AiAnalysisResult} + file metadata when MongoDB save fails.</p>
 *
 * @param id                      MongoDB document {@code _id}, or {@code ""} if storage failed
 * @param fileName                original uploaded PDF filename
 * @param fileSizeBytes           PDF file size in bytes
 * @param atsScore                overall ATS alignment score (0–100)
 * @param matchedKeywords         skills/tools present in both resume and JD
 * @param missingKeywords         high-value JD terms absent from the resume
 * @param bulletPointImprovements 3 rewritten bullet points with reasons
 * @param analyzedAt              server-side analysis timestamp
 */
public record AnalysisResponse(
        String id,
        String fileName,
        long fileSizeBytes,
        int atsScore,
        List<String> matchedKeywords,
        List<String> missingKeywords,
        List<BulletImprovement> bulletPointImprovements,
        LocalDateTime analyzedAt
) {
}
