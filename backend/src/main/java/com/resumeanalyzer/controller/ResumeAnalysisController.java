// backend/src/main/java/com/resumeanalyzer/controller/ResumeAnalysisController.java
package com.resumeanalyzer.controller;

import com.resumeanalyzer.dto.AiAnalysisResult;
import com.resumeanalyzer.dto.AnalysisResponse;
import com.resumeanalyzer.dto.PaginatedResponse;
import com.resumeanalyzer.exception.UnsupportedMediaTypeException;
import com.resumeanalyzer.model.ResumeAnalysis;
import com.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.resumeanalyzer.service.MongoStorageService;
import com.resumeanalyzer.service.GeminiService;
import com.resumeanalyzer.service.PdfExtractionService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for resume analysis operations.
 */
@RestController
@RequestMapping("/api/v1")
@Validated 
public class ResumeAnalysisController {

    private static final Logger log = LoggerFactory.getLogger(ResumeAnalysisController.class);

    private final PdfExtractionService pdfExtractionService;
    private final GeminiService geminiService;
    private final MongoStorageService mongoStorageService;
    private final ResumeAnalysisRepository repository;

    public ResumeAnalysisController(PdfExtractionService pdfExtractionService,
                                    GeminiService geminiService,
                                    MongoStorageService mongoStorageService,
                                    ResumeAnalysisRepository repository) {
        this.pdfExtractionService = pdfExtractionService;
        this.geminiService = geminiService;
        this.mongoStorageService = mongoStorageService;
        this.repository = repository;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeResume(
            @RequestPart("resume") MultipartFile resume,
            @RequestParam("jobDescription")
            @NotBlank(message = "Job description is required.")
            @Size(min = 50, max = 5000,
                    message = "Job description must be between 50 and 5000 characters.")
            String jobDescription) {

        String contentType = resume.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new UnsupportedMediaTypeException(
                    "Only PDF files are accepted. Received: " + contentType);
        }

        log.info("Analyzing resume '{}' ({} bytes) against JD ({} chars).",
                resume.getOriginalFilename(), resume.getSize(), jobDescription.length());

        String resumeText = pdfExtractionService.extractText(resume);

        AiAnalysisResult aiResult = geminiService.analyzeResume(resumeText, jobDescription);

        String savedId = mongoStorageService.saveAnalysis(
                aiResult,
                resume.getOriginalFilename(),
                resume.getSize(),
                jobDescription
        );

        AnalysisResponse response = new AnalysisResponse(
                savedId,
                resume.getOriginalFilename(),
                resume.getSize(),
                aiResult.atsScore(),
                aiResult.matchedKeywords(),
                aiResult.missingKeywords(),
                aiResult.bulletPointImprovements(),
                LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analyses")
    public ResponseEntity<PaginatedResponse<AnalysisResponse>> getAnalyses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ResumeAnalysis> analysisPage = repository.findAllByOrderByAnalyzedAtDesc(
                PageRequest.of(page, size));

        List<AnalysisResponse> content = analysisPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        PaginatedResponse<AnalysisResponse> response = new PaginatedResponse<>(
                content,
                analysisPage.getNumber(),
                analysisPage.getSize(),
                analysisPage.getTotalElements(),
                analysisPage.getTotalPages()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analyses/{id}")
    public ResponseEntity<AnalysisResponse> getAnalysisById(@PathVariable String id) {
        Optional<ResumeAnalysis> analysis = repository.findById(id);

        return analysis
                .map(a -> ResponseEntity.ok(toResponse(a)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/analyses/{id}")
    public ResponseEntity<Void> deleteAnalysis(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        log.info("Deleted analysis record: {}", id);
        return ResponseEntity.noContent().build();
    }

    private AnalysisResponse toResponse(ResumeAnalysis analysis) {
        return new AnalysisResponse(
                analysis.getId(),
                analysis.getFileName(),
                analysis.getFileSizeBytes(),
                analysis.getAtsScore(),
                analysis.getMatchedKeywords(),
                analysis.getMissingKeywords(),
                analysis.getBulletPointImprovements(),
                analysis.getAnalyzedAt()
        );
    }
}
