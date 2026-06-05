// backend/src/main/java/com/resumeanalyzer/config/CorsConfig.java
package com.resumeanalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS configuration allowing the Vite dev server (or production frontend) to call the API.
 *
 * <p>Allowed origin is read from {@code app.cors.allowed-origin} in {@code application.yml},
 * defaulting to {@code http://localhost:5173} (Vite's default dev server port).</p>
 *
 * <p><b>Allowed methods:</b> GET, POST, DELETE, OPTIONS, HEAD</p>
 * <ul>
 *   <li>GET, POST, DELETE — core API operations</li>
 *   <li>OPTIONS — CORS preflight requests (browser-sent automatically)</li>
 *   <li>HEAD — proxy health checks and some browser prefetching</li>
 * </ul>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origin:http://localhost:5173}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigin)
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS", "HEAD")
                .allowedHeaders("*")
                .allowCredentials(false)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
}
