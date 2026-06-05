// backend/src/main/java/com/resumeanalyzer/dto/AiAnalysisResult.java
package com.resumeanalyzer.dto;

import com.resumeanalyzer.model.BulletImprovement;

import java.util.List;

/**
 * Maps the structured JSON response from the OpenAI gpt-4o-mini model.
 *
 * <p>This is the inner content parsed from {@code choices[0].message.content},
 * NOT the outer OpenAI API wrapper. The expected JSON schema:</p>
 * <pre>
 * {
 *   "atsScore": 72,
 *   "matchedKeywords": ["Java", "Spring Boot", ...],
 *   "missingKeywords": ["Kubernetes", "Terraform", ...],
 *   "bulletPointImprovements": [
 *     { "original": "...", "suggested": "...", "reason": "..." }
 *   ]
 * }
 * </pre>
 *
 * @param atsScore                overall resume-JD alignment score (0–100)
 * @param matchedKeywords         skills/tools/terms present in both resume and JD (3–20 items)
 * @param missingKeywords         high-value JD terms absent from the resume (3–15 items)
 * @param bulletPointImprovements exactly 3 rewritten bullet points targeting the weakest ones
 */
public record AiAnalysisResult(
        int atsScore,
        List<String> matchedKeywords,
        List<String> missingKeywords,
        List<BulletImprovement> bulletPointImprovements
) {
}
