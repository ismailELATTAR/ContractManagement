package com.bp.contractrepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Contract Repository & Lifecycle Management System
 * Main Spring Boot Application for Banque Populaire
 *
 * This application provides:
 * - Contract management and lifecycle tracking
 * - Integration with T24/Evolan core banking systems
 * - Document management with security classification
 * - Automated renewal reminders and expiration alerts
 * - Comprehensive audit trail and reporting
 *
 * @author Contract Management Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableScheduling
@EnableAsync
@EnableTransactionManagement
@ConfigurationPropertiesScan
public class ContractRepositoryApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ContractRepositoryApplication.class);

        // Set default profile if none specified
        app.setDefaultProperties(java.util.Map.of(
                "spring.profiles.default", "dev"
        ));

        // Add custom banner
        app.setBannerMode(org.springframework.boot.Banner.Mode.CONSOLE);

        // Start the application
        app.run(args);
    }
}