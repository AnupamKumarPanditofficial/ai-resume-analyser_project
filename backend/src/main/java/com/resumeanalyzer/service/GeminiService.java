// backend/src/main/java/com/resumeanalyzer/service/GeminiService.java
package com.resumeanalyzer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.config.GeminiConfig;
import com.resumeanalyzer.dto.AiAnalysisResult;
import com.resumeanalyzer.exception.AiResponseParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Integrates with the Google Gemini API to perform ATS resume analysis.
 * Features rate limit checks and parsing tailored to the Gemini JSON response structure.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final int MAX_RETRIES = 1; // Kept to 1 retry to minimize API usage
    private static final long[] RETRY_DELAYS_MS = {2000L};

    private static final String SYSTEM_PROMPT = """
            You are an elite ATS (Applicant Tracking System) engine and career coach.
            Analyze the provided Candidate Resume against the Job Description.
            Return ONLY a valid, minified JSON object. No markdown fences, no explanation, no preamble.

            Schema:
            {
              "atsScore": <integer 0–100, representing overall resume-JD alignment>,
              "matchedKeywords": [<skills/tools/terms present in both resume and JD>],
              "missingKeywords": [<high-value JD terms absent from the resume>],
              "bulletPointImprovements": [
                {
                  "original": <original resume bullet point, verbatim>,
                  "suggested": <rewritten using STAR method with metrics where inferable>,
                  "reason": <one sentence: what was improved and why>
                }
              ]
            }

            Rules:
            - atsScore must reflect keyword density, skill alignment, and seniority match.
            - matchedKeywords: minimum 3, maximum 20 items.
            - missingKeywords: minimum 3, maximum 15 items.
            - bulletPointImprovements: exactly 3, targeting the weakest bullet points.
            - Never fabricate experience. Only rewrite what exists in the resume.""";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final GeminiConfig geminiConfig;

    public GeminiService(ObjectMapper objectMapper, GeminiConfig geminiConfig) {
        this.objectMapper = objectMapper;
        this.geminiConfig = geminiConfig;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public AiAnalysisResult analyzeResume(String resumeText, String jobDescription) {
        String userMessage = "[Candidate Resume]\n" + resumeText + "\n\n[Job Description]\n" + jobDescription;
        String requestBody = buildRequestBody(userMessage);

        String rawApiResponse = executeWithRetry(requestBody);
        String contentJson = extractContentFromApiResponse(rawApiResponse);
        AiAnalysisResult result = parseAnalysisResult(contentJson);
        validateResult(result, contentJson);

        log.info("Gemini analysis complete. ATS Score: {}, Matched: {}, Missing: {}",
                result.atsScore(), result.matchedKeywords().size(), result.missingKeywords().size());

        return result;
    }

    private String buildRequestBody(String userMessage) {
        try {
            Map<String, Object> request = Map.of(
                    "systemInstruction", Map.of(
                            "parts", List.of(Map.of("text", SYSTEM_PROMPT))
                    ),
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(Map.of("text", userMessage))
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0, // Deterministic
                            "responseMimeType", "application/json"
                    )
            );
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new AiResponseParseException("Failed to serialize Gemini request body.", "", e);
        }
    }

    private String executeWithRetry(String requestBody) {
        int attempt = 0;
        String lastResponseBody = "";
        
        // Gemini API URL format: https://generativelanguage.googleapis.com/v1beta/models/MODEL_NAME:generateContent?key=API_KEY
        String apiUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", 
                geminiConfig.getModel(), geminiConfig.getApiKey());

        while (attempt <= MAX_RETRIES) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .timeout(Duration.ofSeconds(60))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                lastResponseBody = response.body();
                int statusCode = response.statusCode();

                if (statusCode >= 200 && statusCode < 300) {
                    return lastResponseBody;
                }

                if (statusCode == 429 || statusCode >= 500) {
                    if (attempt < MAX_RETRIES) {
                        long delayMs = RETRY_DELAYS_MS[attempt];
                        log.warn("Gemini HTTP {}. Retry after {}ms. Body: {}", statusCode, delayMs, truncate(lastResponseBody, 200));
                        Thread.sleep(delayMs);
                        attempt++;
                        continue;
                    }
                    throw new AiResponseParseException("Gemini returned HTTP " + statusCode, lastResponseBody);
                }

                throw new AiResponseParseException("Gemini API returned non-retriable HTTP " + statusCode, lastResponseBody);

            } catch (AiResponseParseException e) {
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AiResponseParseException("Interrupted", lastResponseBody, e);
            } catch (IOException e) {
                if (attempt < MAX_RETRIES) {
                    long delayMs = RETRY_DELAYS_MS[attempt];
                    try { Thread.sleep(delayMs); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                    attempt++;
                } else {
                    throw new AiResponseParseException("Network error calling Gemini", lastResponseBody, e);
                }
            }
        }
        throw new AiResponseParseException("Gemini request failed", lastResponseBody);
    }

    private String extractContentFromApiResponse(String rawApiResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawApiResponse);

            JsonNode candidates = root.get("candidates");
            if (candidates == null || !candidates.isArray() || candidates.isEmpty()) {
                throw new AiResponseParseException("Missing 'candidates'", rawApiResponse);
            }

            JsonNode content = candidates.get(0).get("content");
            if (content == null) {
                throw new AiResponseParseException("Missing 'content'", rawApiResponse);
            }

            JsonNode parts = content.get("parts");
            if (parts == null || !parts.isArray() || parts.isEmpty()) {
                throw new AiResponseParseException("Missing 'parts'", rawApiResponse);
            }

            JsonNode textNode = parts.get(0).get("text");
            if (textNode == null || !textNode.isTextual()) {
                throw new AiResponseParseException("Missing 'text'", rawApiResponse);
            }

            String contentText = textNode.asText().trim();
            if (contentText.startsWith("```")) {
                contentText = contentText
                        .replaceFirst("^```(?:json)?\\s*", "")
                        .replaceFirst("\\s*```$", "")
                        .trim();
            }

            return contentText;
        } catch (JsonProcessingException e) {
            throw new AiResponseParseException("Failed to parse Gemini JSON", rawApiResponse, e);
        }
    }

    private AiAnalysisResult parseAnalysisResult(String contentJson) {
        try {
            return objectMapper.readValue(contentJson, AiAnalysisResult.class);
        } catch (JsonProcessingException e) {
            throw new AiResponseParseException("Failed to map JSON to AiAnalysisResult", contentJson, e);
        }
    }

    private void validateResult(AiAnalysisResult result, String contentJson) {
        if (result.atsScore() < 0 || result.atsScore() > 100) {
            throw new AiResponseParseException("atsScore out of range", contentJson);
        }
        if (result.matchedKeywords() == null || result.matchedKeywords().isEmpty()) {
            throw new AiResponseParseException("matchedKeywords is empty", contentJson);
        }
        if (result.missingKeywords() == null || result.missingKeywords().isEmpty()) {
            throw new AiResponseParseException("missingKeywords is empty", contentJson);
        }
        if (result.bulletPointImprovements() == null || result.bulletPointImprovements().isEmpty()) {
            throw new AiResponseParseException("bulletPointImprovements is empty", contentJson);
        }
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "null";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
