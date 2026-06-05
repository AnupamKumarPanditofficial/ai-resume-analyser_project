// backend/src/main/java/com/resumeanalyzer/repository/ResumeAnalysisRepository.java
package com.resumeanalyzer.repository;

import com.resumeanalyzer.model.ResumeAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for {@link ResumeAnalysis} documents.
 *
 * <p>Provides standard CRUD via {@link MongoRepository} plus custom query methods:</p>
 * <ul>
 *   <li>{@link #findAllByOrderByAnalyzedAtDesc} — paginated listing, newest first (for the history panel).</li>
 *   <li>{@link #findByAtsScoreGreaterThanEqual} — filter by minimum ATS score (for analytics/filtering).</li>
 * </ul>
 */
@Repository
public interface ResumeAnalysisRepository extends MongoRepository<ResumeAnalysis, String> {

    /**
     * Fetches all analysis records ordered by analysis date descending (newest first),
     * with Spring Data pagination support.
     *
     * @param pageable pagination parameters (page number, page size)
     * @return a page of {@link ResumeAnalysis} documents
     */
    Page<ResumeAnalysis> findAllByOrderByAnalyzedAtDesc(Pageable pageable);

    /**
     * Finds all analysis records with an ATS score at or above the given threshold.
     * Useful for filtering high-scoring analyses.
     *
     * @param minScore minimum ATS score (inclusive), range 0–100
     * @return list of matching {@link ResumeAnalysis} documents (unordered)
     */
    List<ResumeAnalysis> findByAtsScoreGreaterThanEqual(int minScore);
}
