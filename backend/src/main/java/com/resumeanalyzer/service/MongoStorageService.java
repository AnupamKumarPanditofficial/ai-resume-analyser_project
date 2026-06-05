// backend/src/main/java/com/resumeanalyzer/service/MongoStorageService.java
package com.resumeanalyzer.service;

import com.resumeanalyzer.dto.AiAnalysisResult;
import com.resumeanalyzer.model.ResumeAnalysis;
import com.resumeanalyzer.repository.ResumeAnalysisRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Persists AI analysis results to MongoDB.
 *
 * <p><b>Fault tolerance:</b> If MongoDB is unreachable or the write fails for any reason,
 * the error is logged but the analysis result is still returned to the frontend.
 * Storage failure must <b>never</b> degrade the user-facing response.</p>
 *
 * <p>On failure, returns an empty string {@code ""} for the document ID instead of {@code null}
 * to prevent {@link NullPointerException} on the frontend when rendering "Analysis ID: ...".</p>
 */
@Service
public class MongoStorageService {

    private static final Logger log = LoggerFactory.getLogger(MongoStorageService.class);

    /** Maximum length of the job description snippet stored in MongoDB. */
    private static final int JD_SNIPPET_MAX_LENGTH = 200;

    private final ResumeAnalysisRepository repository;

    public MongoStorageService(ResumeAnalysisRepository repository) {
        this.repository = repository;
    }

    /**
     * Maps an AI analysis result and file metadata to a {@link ResumeAnalysis} document
     * and persists it to MongoDB.
     *
     * @param analysisResult the parsed AI analysis output
     * @param fileName       original PDF filename
     * @param fileSizeBytes  PDF file size in bytes
     * @param jobDescription the full job description (only first 200 chars are stored)
     * @return the saved document's MongoDB {@code _id}, or {@code ""} if the save failed
     */
    public String saveAnalysis(AiAnalysisResult analysisResult,
                               String fileName,
                               long fileSizeBytes,
                               String jobDescription) {
        try {
            ResumeAnalysis document = new ResumeAnalysis();
            document.setFileName(fileName);
            document.setFileSizeBytes(fileSizeBytes);
            document.setJobDescriptionSnippet(truncateSnippet(jobDescription));
            document.setAtsScore(analysisResult.atsScore());
            document.setMatchedKeywords(analysisResult.matchedKeywords());
            document.setMissingKeywords(analysisResult.missingKeywords());
            document.setBulletPointImprovements(analysisResult.bulletPointImprovements());
            document.setAnalyzedAt(LocalDateTime.now());

            ResumeAnalysis saved = repository.save(document);

            log.info("Analysis saved to MongoDB with id: {}", saved.getId());
            return saved.getId();

        } catch (DataAccessException e) {
            // MongoDB is down or write failed — log and return gracefully.
            // The analysis result is still returned to the user.
            log.error("Failed to save analysis to MongoDB. Storage failure will not degrade "
                    + "the user response. Error: {}", e.getMessage(), e);
            return "";
        }
    }

    /**
     * Truncates the job description to the configured snippet length.
     * Only the first {@value #JD_SNIPPET_MAX_LENGTH} characters are stored
     * for privacy and storage efficiency.
     *
     * @param jobDescription the full job description
     * @return truncated snippet (max 200 chars)
     */
    private String truncateSnippet(String jobDescription) {
        if (jobDescription == null) {
            return "";
        }
        if (jobDescription.length() <= JD_SNIPPET_MAX_LENGTH) {
            return jobDescription;
        }
        return jobDescription.substring(0, JD_SNIPPET_MAX_LENGTH) + "...";
    }
}
