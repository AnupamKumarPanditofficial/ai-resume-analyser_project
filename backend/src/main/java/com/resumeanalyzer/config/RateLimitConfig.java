// backend/src/main/java/com/resumeanalyzer/config/RateLimitConfig.java
package com.resumeanalyzer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Externalizes rate-limiting configuration from {@code application.yml}.
 *
 * <p>Bound to the {@code rate-limit} prefix:</p>
 * <pre>
 * rate-limit:
 *   requests-per-minute: 5
 * </pre>
 */
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
public class RateLimitConfig {

    /**
     * Maximum number of requests allowed per IP address within a 60-second sliding window.
     * Default: 5.
     */
    private int requestsPerMinute = 5;

    public int getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(int requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }
}
