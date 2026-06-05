// backend/src/main/java/com/resumeanalyzer/config/MongoConfig.java
package com.resumeanalyzer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 *
 * <p>Index creation is handled by annotations on the model classes
 * ({@code @Indexed}, {@code @CompoundIndex}) combined with
 * {@code spring.data.mongodb.auto-index-creation: true} in application.yml.</p>
 *
 * <p>This class enables repository scanning for the {@code com.resumeanalyzer.repository} package.
 * While Spring Boot auto-detects repositories by default, the explicit annotation makes the
 * scan scope visible and prevents accidental pickup of repositories from other packages.</p>
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.resumeanalyzer.repository")
public class MongoConfig {
    // No programmatic index creation needed — annotations on ResumeAnalysis handle it.
    // See ResumeAnalysis.java for:
    //   - @Indexed(expireAfterSeconds = 2592000) on analyzedAt  → 30-day TTL
    //   - @CompoundIndex(def = "{'atsScore': 1, 'analyzedAt': -1}") on class → sorted queries
}
