package com.bp.contractrepository.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller for Contract Repository System
 * Provides basic health status and system information
 *
 * @author Contract Management Team
 */
@RestController
@RequestMapping("/health")
public class HealthCheckController implements HealthIndicator {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.application.name:contract-repository-system}")
    private String applicationName;

    @Value("${app.version:1.0.0}")
    private String applicationVersion;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    /**
     * Basic health check endpoint
     * Returns 200 OK if application is running
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> appHealth() {
        Map<String, Object> healthStatus = new HashMap<>();

        healthStatus.put("status", "UP");
        healthStatus.put("application", applicationName);
        healthStatus.put("version", applicationVersion);
        healthStatus.put("profile", activeProfile);
        healthStatus.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        healthStatus.put("message", "Contract Repository System is running successfully");

        // Add database connectivity check
        boolean dbConnected = checkDatabaseConnection();
        healthStatus.put("database", dbConnected ? "UP" : "DOWN");

        // Add system info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> systemInfo = new HashMap<>();
        systemInfo.put("javaVersion", System.getProperty("java.version"));
        systemInfo.put("totalMemory", runtime.totalMemory() / 1024 / 1024 + " MB");
        systemInfo.put("freeMemory", runtime.freeMemory() / 1024 / 1024 + " MB");
        systemInfo.put("processors", runtime.availableProcessors());

        healthStatus.put("system", systemInfo);

        return ResponseEntity.ok(healthStatus);
    }

    /**
     * Simple ping endpoint
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    /**
     * Application info endpoint
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> info = new HashMap<>();

        info.put("application", applicationName);
        info.put("version", applicationVersion);
        info.put("description", "Enterprise contract management system for Banque Populaire");
        info.put("profile", activeProfile);
        info.put("buildTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        Map<String, String> team = new HashMap<>();
        team.put("name", "Contract Management Team");
        team.put("organization", "Banque Populaire");
        team.put("contact", "contract-mgmt@banquepopulaire.ma");

        info.put("team", team);

        Map<String, String> features = new HashMap<>();
        features.put("contractManagement", "CRUD operations for contracts");
        features.put("customerIntegration", "T24/Evolan integration for customer lookup");
        features.put("documentManagement", "File upload/download for contracts");
        features.put("auditTrail", "Complete audit history tracking");
        features.put("lifecycleManagement", "Automated renewal reminders");
        features.put("reporting", "Dashboard and basic reporting");

        info.put("features", features);

        return ResponseEntity.ok(info);
    }

    /**
     * Spring Boot Actuator Health Indicator implementation
     */
    @Override
    public Health health() {
        boolean dbConnected = checkDatabaseConnection();

        if (dbConnected) {
            return Health.up()
                    .withDetail("application", applicationName)
                    .withDetail("version", applicationVersion)
                    .withDetail("profile", activeProfile)
                    .withDetail("database", "Connected")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        } else {
            return Health.down()
                    .withDetail("application", applicationName)
                    .withDetail("database", "Disconnected")
                    .withDetail("timestamp", LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Check database connectivity
     */
    private boolean checkDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(5); // 5 second timeout
        } catch (Exception e) {
            return false;
        }
    }
}