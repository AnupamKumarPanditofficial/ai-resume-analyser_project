// backend/src/main/java/com/resumeanalyzer/service/PdfExtractionService.java
package com.resumeanalyzer.service;

import com.resumeanalyzer.exception.FileTooLargeException;
import com.resumeanalyzer.exception.PdfExtractionException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts plain text from uploaded PDF files.
 *
 * <p><b>Extraction strategy (two-tier):</b></p>
 * <ol>
 *   <li><b>PDFBox (primary):</b> Fast, handles most text-based PDFs.</li>
 *   <li><b>Apache Tika (fallback):</b> Invoked when PDFBox returns blank/null/empty text.
 *       Tika uses a more aggressive parsing pipeline and can sometimes extract text from
 *       PDFs with non-standard encoding or embedded fonts.</li>
 * </ol>
 *
 * <p><b>Constraints enforced:</b></p>
 * <ul>
 *   <li>Max file size: 5 MB — rejects with {@link FileTooLargeException} (HTTP 413).</li>
 *   <li>Max extracted text: 15,000 characters — truncates with a WARN log if exceeded.</li>
 *   <li>Whitespace normalization: collapses multiple consecutive whitespace/newlines, trims.</li>
 * </ul>
 */
@Service
public class PdfExtractionService {

    private static final Logger log = LoggerFactory.getLogger(PdfExtractionService.class);

    /** Maximum allowed file size: 5 MB in bytes. */
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    /** Maximum extracted text length before truncation. */
    private static final int MAX_TEXT_LENGTH = 15_000;

    /** Singleton Tika instance (thread-safe, reusable). */
    private final Tika tika = new Tika();

    /**
     * Extracts text from a PDF {@link MultipartFile}.
     *
     * @param file the uploaded PDF file
     * @return normalized, possibly truncated plain text content
     * @throws FileTooLargeException   if the file exceeds 5 MB
     * @throws PdfExtractionException  if both PDFBox and Tika fail to extract text
     */
    public String extractText(MultipartFile file) {
        // ── Validate file size ──
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new FileTooLargeException(file.getSize(), MAX_FILE_SIZE_BYTES);
        }

        String text = extractWithPdfBox(file);

        // ── Fall back to Tika if PDFBox returned nothing useful ──
        if (text == null || text.isBlank()) {
            log.info("PDFBox returned blank text for '{}'. Falling back to Apache Tika.",
                    file.getOriginalFilename());
            text = extractWithTika(file);
        }

        // ── Final validation — if both extractors failed ──
        if (text == null || text.isBlank()) {
            throw new PdfExtractionException(
                    "Unable to extract text from PDF '" + file.getOriginalFilename()
                            + "'. The file may be a scanned image without OCR text layer."
            );
        }

        // ── Normalize whitespace ──
        text = normalizeWhitespace(text);

        // ── Truncate if too long ──
        if (text.length() > MAX_TEXT_LENGTH) {
            log.warn("Extracted text from '{}' is {} chars (limit: {}). Truncating.",
                    file.getOriginalFilename(), text.length(), MAX_TEXT_LENGTH);
            text = text.substring(0, MAX_TEXT_LENGTH);
        }

        log.info("Successfully extracted {} chars from '{}'.", text.length(), file.getOriginalFilename());
        return text;
    }

    /**
     * Primary extraction using Apache PDFBox.
     *
     * @param file the PDF file
     * @return extracted text, or null/blank if extraction fails
     */
    private String extractWithPdfBox(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);

        } catch (IOException e) {
            log.warn("PDFBox extraction failed for '{}': {}",
                    file.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

    /**
     * Fallback extraction using Apache Tika.
     *
     * <p>Tika's auto-detect parser can handle a wider range of PDF encodings
     * than raw PDFBox, though it won't perform true OCR without Tesseract.</p>
     *
     * @param file the PDF file
     * @return extracted text, or null if extraction fails
     */
    private String extractWithTika(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return tika.parseToString(inputStream);
        } catch (Exception e) {
            log.warn("Tika extraction also failed for '{}': {}",
                    file.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

    /**
     * Normalizes whitespace in extracted text:
     * <ul>
     *   <li>Collapses 3+ consecutive newlines into 2 (preserves paragraph breaks).</li>
     *   <li>Collapses multiple consecutive spaces/tabs into a single space.</li>
     *   <li>Trims leading/trailing whitespace.</li>
     * </ul>
     *
     * @param text raw extracted text
     * @return normalized text
     */
    private String normalizeWhitespace(String text) {
        // Collapse 3+ newlines into exactly 2 (preserve paragraph breaks)
        text = text.replaceAll("(\\r?\\n){3,}", "\n\n");
        // Collapse multiple spaces/tabs into single space
        text = text.replaceAll("[ \\t]+", " ");
        // Trim each line
        text = text.lines()
                .map(String::trim)
                .reduce((a, b) -> a + "\n" + b)
                .orElse("");
        return text.trim();
    }
}
