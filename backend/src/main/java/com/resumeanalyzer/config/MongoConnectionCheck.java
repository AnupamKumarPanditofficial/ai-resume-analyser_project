// backend/src/main/java/com/resumeanalyzer/config/MongoConnectionCheck.java
package com.resumeanalyzer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * Runs automatically on Spring Boot startup to verify and log MongoDB connection status.
 */
@Component
public class MongoConnectionCheck implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoConnectionCheck.class);
    private final MongoTemplate mongoTemplate;

    public MongoConnectionCheck(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(String... args) {
        try {
            // Ping the database to ensure connection is alive
            mongoTemplate.executeCommand("{ ping: 1 }");
            
            // Get database name to show in the log
            String dbName = mongoTemplate.getDb().getName();
            
            log.info("\n" +
                    "=========================================================\n" +
                    " ✅ MONGODB CONNECTED SUCCESSFULLY! \n" +
                    " Database Name: " + dbName + "\n" +
                    "=========================================================");
        } catch (Exception e) {
            log.error("\n" +
                    "=========================================================\n" +
                    " ❌ MONGODB CONNECTION FAILED! \n" +
                    " Please check if MongoDB is running on localhost:27017 \n" +
                    " or check your MONGODB_URI in Render. \n" +
                    " Error: " + e.getMessage() + "\n" +
                    "=========================================================");
        }
    }
}
