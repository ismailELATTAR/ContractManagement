package com.bp.contractrepository.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer Data Transfer Object
 * Used for customer information from T24/Evolan integration
 *
 * @author Contract Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer information from core banking systems")
public class CustomerDTO {

    @Schema(description = "Internal customer identifier", example = "CUS-12345")
    private String customerId;

    @Schema(description = "Customer name", example = "Microsoft Maroc SARL")
    private String customerName;

    @Schema(description = "Customer type", example = "CORPORATE")
    private String customerType;

    @Schema(description = "Primary contact person", example = "Ahmed Benjelloun")
    private String contactPerson;

    @Schema(description = "Contact email", example = "ahmed.benjelloun@microsoft.ma")
    private String contactEmail;

    @Schema(description = "Contact phone", example = "+212 522 123 456")
    private String contactPhone;

    @Schema(description = "Customer address", example = "Twin Center, Boulevard Zerktouni, Casablanca")
    private String address;

    @Schema(description = "City", example = "Casablanca")
    private String city;

    @Schema(description = "Country", example = "Morocco")
    private String country;

    @Schema(description = "Relationship manager", example = "Fatima El Alami")
    private String relationshipManager;

    @Schema(description = "Account manager", example = "Youssef Sekkouri")
    private String accountManager;

    @Schema(description = "Customer is active", example = "true")
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last synchronization with core banking", example = "2024-01-15T10:30:00")
    private LocalDateTime lastSyncDate;

    // T24/Evolan Integration Fields
    @Schema(description = "T24 core banking customer ID", example = "T24-CUS-001")
    private String t24CustomerId;

    @Schema(description = "Source system", example = "T24")
    private String sourceSystem;

    @Schema(description = "Customer risk rating", example = "LOW")
    private String riskRating;

    @Schema(description = "Banking sector classification", example = "TECHNOLOGY")
    private String sector;

    @Schema(description = "Tax identification number", example = "123456789")
    private String taxId;

    @Schema(description = "Legal entity identifier", example = "LEI-123456789")
    private String legalEntityId;

    /**
     * Check if customer is active and valid for contracts
     */
    public boolean isValidForContracts() {
        return Boolean.TRUE.equals(isActive) && customerName != null && !customerName.trim().isEmpty();
    }

    /**
     * Check if customer data is recently synced (within last 30 days)
     */
    public boolean isRecentlySynced() {
        if (lastSyncDate == null) {
            return false;
        }
        return lastSyncDate.isAfter(LocalDateTime.now().minusDays(30));
    }

    /**
     * Get display name for UI
     */
    public String getDisplayName() {
        if (customerName != null) {
            return customerId != null ? customerName + " (" + customerId + ")" : customerName;
        }
        return customerId != null ? customerId : "Unknown Customer";
    }

    /**
     * Check if this is a T24 customer
     */
    public boolean isT24Customer() {
        return "T24".equalsIgnoreCase(sourceSystem) || t24CustomerId != null;
    }

    /**
     * Check if this is an Evolan customer
     */
    public boolean isEvolanCustomer() {
        return "EVOLAN".equalsIgnoreCase(sourceSystem);
    }

    /**
     * Check if customer has complete contact information
     */
    public boolean hasCompleteContactInfo() {
        return contactPerson != null && !contactPerson.trim().isEmpty() &&
                (contactEmail != null && !contactEmail.trim().isEmpty() ||
                        contactPhone != null && !contactPhone.trim().isEmpty());
    }
}