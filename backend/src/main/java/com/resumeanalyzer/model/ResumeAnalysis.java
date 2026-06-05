// backend/src/main/java/com/resumeanalyzer/model/ResumeAnalysis.java
package com.resumeanalyzer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB document model storing resume analysis metadata and results.
 *
 * <p><b>Design decisions:</b></p>
 * <ul>
 *   <li>Raw resume text and PDF binary are NOT stored — only metadata + analysis output.</li>
 *   <li>Full job description is NOT stored — only the first 200 chars as a snippet for display.</li>
 *   <li>TTL index on {@code analyzedAt} auto-expires documents after 30 days (2,592,000 seconds).</li>
 *   <li>Compound index on {@code (atsScore, analyzedAt)} enables efficient sorted queries
 *       (e.g., "find all analyses with score ≥ 75, newest first").</li>
 * </ul>
 *
 * <p>Indexes are declared via annotations (idempotent on startup) rather than programmatic
 * {@code ensureIndex()} calls, which can fail silently or conflict if the index definition changes.</p>
 */
@Document(collection = "resume_analyses")
@CompoundIndex(name = "idx_score_date", def = "{'atsScore': 1, 'analyzedAt': -1}")
public class ResumeAnalysis {

    @Id
    private String id;

    /** Original PDF filename as uploaded by the client. */
    private String fileName;

    /** PDF file size in bytes — used for display, not storage of the file itself. */
    private long fileSizeBytes;

    /** First 200 characters of the job description — truncated for privacy and storage efficiency. */
    private String jobDescriptionSnippet;

    /** Overall ATS alignment score (0–100) computed by the AI model. */
    private int atsScore;

    /** Skills, tools, and terms present in both the resume and job description. */
    private List<String> matchedKeywords;

    /** High-value JD terms absent from the resume. */
    private List<String> missingKeywords;

    /** Exactly 3 bullet-point improvement suggestions targeting the weakest points. */
    private List<BulletImprovement> bulletPointImprovements;

    /**
     * Server-side timestamp of when the analysis was performed.
     * <p>TTL index: documents expire automatically 30 days after this timestamp.
     * 30 days = 2,592,000 seconds.</p>
     */
    @Indexed(expireAfterSeconds = 2592000)
    private LocalDateTime analyzedAt;

    // ==================== Constructors ====================

    public ResumeAnalysis() {
    }

    // ==================== Getters & Setters ====================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getJobDescriptionSnippet() {
        return jobDescriptionSnippet;
    }

    public void setJobDescriptionSnippet(String jobDescriptionSnippet) {
        this.jobDescriptionSnippet = jobDescriptionSnippet;
    }

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public List<String> getMatchedKeywords() {
        return matchedKeywords;
    }

    public void setMatchedKeywords(List<String> matchedKeywords) {
        this.matchedKeywords = matchedKeywords;
    }

    public List<String> getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(List<String> missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public List<BulletImprovement> getBulletPointImprovements() {
        return bulletPointImprovements;
    }

    public void setBulletPointImprovements(List<BulletImprovement> bulletPointImprovements) {
        this.bulletPointImprovements = bulletPointImprovements;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
