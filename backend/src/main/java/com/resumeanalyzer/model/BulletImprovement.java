// backend/src/main/java/com/resumeanalyzer/model/BulletImprovement.java
package com.resumeanalyzer.model;

/**
 * Embedded POJO representing a single bullet-point improvement suggestion.
 * Stored as a nested document inside {@link ResumeAnalysis} — NOT a separate collection.
 *
 * @param original  the original resume bullet point, verbatim from the candidate's resume
 * @param suggested the rewritten version using the STAR method with metrics where inferable
 * @param reason    one-sentence explanation of what was improved and why
 */
public record BulletImprovement(
        String original,
        String suggested,
        String reason
) {
}
