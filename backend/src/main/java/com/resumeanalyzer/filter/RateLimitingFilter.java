// backend/src/main/java/com/resumeanalyzer/filter/RateLimitingFilter.java
package com.resumeanalyzer.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumeanalyzer.config.RateLimitConfig;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-IP rate limiting filter using Bucket4j 8.x (token bucket algorithm).
 *
 * <p><b>Behavior:</b></p>
 * <ul>
 *   <li>Each unique client IP gets its own bucket with {@code N} tokens per 60-second window.</li>
 *   <li>If a request exhausts all tokens, the filter short-circuits with HTTP 429,
 *       a {@code Retry-After} header, and the standard {@code ErrorResponse} JSON body.</li>
 *   <li>Client IP is extracted from {@code X-Forwarded-For} header (first value) when present,
 *       falling back to {@code request.getRemoteAddr()} for direct connections.</li>
 * </ul>
 *
 * <p><b>Registration:</b> Annotated with {@code @Component} so Spring Boot auto-registers it
 * via component scan. If filter ordering becomes important (multiple filters), switch to
 * {@code FilterRegistrationBean} with explicit {@code setOrder()}.</p>
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final ConcurrentHashMap<String, Bucket> bucketCache = new ConcurrentHashMap<>();
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    public RateLimitingFilter(RateLimitConfig rateLimitConfig, ObjectMapper objectMapper) {
        this.rateLimitConfig = rateLimitConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = resolveClientIp(request);
        Bucket bucket = bucketCache.computeIfAbsent(clientIp, this::createBucket);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Request allowed — add rate limit headers for client visibility
            response.addHeader("X-Rate-Limit-Remaining",
                    String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded — short-circuit the response
            long retryAfterSeconds = Duration.ofNanos(probe.getNanosToWaitForRefill())
                    .toSeconds() + 1; // +1 to avoid edge-case where client retries too early

            log.warn("Rate limit exceeded for IP: {}. Retry after {} seconds.", clientIp, retryAfterSeconds);

            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));

            Map<String, Object> errorBody = new LinkedHashMap<>();
            errorBody.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            errorBody.put("error", "Too Many Requests");
            errorBody.put("message", "Rate limit exceeded. Maximum " + rateLimitConfig.getRequestsPerMinute()
                    + " requests per minute allowed. Please retry after " + retryAfterSeconds + " seconds.");
            errorBody.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

            response.getWriter().write(objectMapper.writeValueAsString(errorBody));
        }
    }

    /**
     * Creates a new token bucket for a client IP.
     *
     * <p>Uses {@link Refill#intervally} for a sliding window:
     * all tokens are refilled at once every 60 seconds.</p>
     *
     * @param clientIp the client IP (used only as the map key, not in bucket logic)
     * @return a new Bucket configured per {@link RateLimitConfig}
     */
    private Bucket createBucket(String clientIp) {
        int tokensPerMinute = rateLimitConfig.getRequestsPerMinute();
        Bandwidth limit = Bandwidth.classic(
                tokensPerMinute,
                Refill.intervally(tokensPerMinute, Duration.ofMinutes(1))
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Resolves the real client IP address.
     *
     * <p>Checks {@code X-Forwarded-For} header first (common behind load balancers/proxies).
     * If present, takes the <b>first</b> IP in the comma-separated list (the original client).
     * Falls back to {@code request.getRemoteAddr()} for direct connections.</p>
     *
     * @param request the incoming HTTP request
     * @return the resolved client IP address
     */
    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For can contain multiple IPs: "client, proxy1, proxy2"
            // The first one is the original client IP.
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
