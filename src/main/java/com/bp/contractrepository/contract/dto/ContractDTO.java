package com.bp.contractrepository.contract.dto;

import com.bp.contractrepository.contract.entity.Contract;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contract Data Transfer Object
 * Used for API responses and data exchange
 *
 * @author Contract Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contract information for API responses")
public class ContractDTO {

    @Schema(description = "Unique contract identifier", example = "1001")
    private Long id;

    @NotBlank(message = "Contract number is required")
    @Size(max = 50, message = "Contract number must not exceed 50 characters")
    @Schema(description = "Unique contract number", example = "BP-2024-001", required = true)
    private String contractNumber;

    @NotBlank(message = "Contract title is required")
    @Size(max = 200, message = "Contract title must not exceed 200 characters")
    @Schema(description = "Contract title", example = "Microsoft Office 365 License Agreement", required = true)
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Detailed contract description", example = "Annual subscription for Microsoft Office 365 Enterprise licenses")
    private String description;

    // Contract Type Information
    @NotNull(message = "Contract type ID is required")
    @Schema(description = "Contract type identifier", example = "101", required = true)
    private Long contractTypeId;

    @Schema(description = "Contract type code", example = "SOFTWARE_LICENSE")
    private String contractTypeCode;

    @Schema(description = "Contract type name", example = "Software License Agreement")
    private String contractTypeName;

    @Schema(description = "Contract type category", example = "IT_SERVICES")
    private String contractTypeCategory;

    // Contract Status
    @NotNull(message = "Contract status is required")
    @Schema(description = "Current contract status", example = "ACTIVE", required = true)
    private Contract.ContractStatus status;

    @Schema(description = "Contract status display name", example = "Active")
    private String statusDisplayName;

    // Customer Information
    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    @Schema(description = "Customer identifier from core banking", example = "CUS-12345", required = true)
    private String customerId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 200, message = "Customer name must not exceed 200 characters")
    @Schema(description = "Customer name", example = "Microsoft Maroc", required = true)
    private String customerName;

    @Size(max = 50, message = "Customer type must not exceed 50 characters")
    @Schema(description = "Customer type", example = "CORPORATE")
    private String customerType;

    @Size(max = 50, message = "T24 customer ID must not exceed 50 characters")
    @Schema(description = "T24 core banking customer ID", example = "T24-CUS-001")
    private String t24CustomerId;

    // Contract Parties
    @NotBlank(message = "Internal department is required")
    @Size(max = 100, message = "Internal department must not exceed 100 characters")
    @Schema(description = "Internal department responsible", example = "IT Department", required = true)
    private String internalDepartment;

    @NotBlank(message = "External party is required")
    @Size(max = 200, message = "External party must not exceed 200 characters")
    @Schema(description = "External contracting party", example = "Microsoft Maroc SARL", required = true)
    private String externalParty;

    // Contract Dates
    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Contract start date", example = "2024-01-01", required = true)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Contract end date", example = "2024-12-31", required = true)
    private LocalDate endDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Contract renewal date", example = "2024-11-01")
    private LocalDate renewalDate;

    // Financial Information
    @DecimalMin(value = "0.0", inclusive = false, message = "Contract value must be positive")
    @Digits(integer = 15, fraction = 2, message = "Contract value format is invalid")
    @Schema(description = "Contract monetary value", example = "500000.00")
    private BigDecimal contractValue;

    @Size(max = 3, message = "Currency must be 3 characters")
    @Schema(description = "Contract currency", example = "MAD")
    private String currency;

    @Schema(description = "Formatted contract value with currency", example = "MAD 500,000.00")
    private String formattedValue;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Schema(description = "Payment terms", example = "Annual payment in advance")
    private String paymentTerms;

    // Risk and Business Information
    @Schema(description = "Contract risk level", example = "MEDIUM")
    private Contract.RiskLevel riskLevel;

    @Schema(description = "Risk level display name", example = "Medium Risk")
    private String riskLevelDisplayName;

    @Size(max = 100, message = "Business owner must not exceed 100 characters")
    @Schema(description = "Business owner responsible for contract", example = "Ahmed Benjelloun")
    private String businessOwner;

    @Size(max = 100, message = "Primary contact must not exceed 100 characters")
    @Schema(description = "Primary contact person", example = "Fatima El Alami")
    private String primaryContact;

    @Size(max = 100, message = "Relationship manager must not exceed 100 characters")
    @Schema(description = "Relationship manager", example = "Youssef Sekkouri")
    private String relationshipManager;

    // Lifecycle Information
    @Schema(description = "Auto-renewal enabled", example = "true")
    private Boolean autoRenewal;

    @Min(value = 1, message = "Reminder days must be positive")
    @Max(value = 365, message = "Reminder days must not exceed 365")
    @Schema(description = "Reminder days before expiration", example = "30")
    private Integer reminderDays;

    @Size(max = 1000, message = "Internal notes must not exceed 1000 characters")
    @Schema(description = "Internal notes (not visible to external parties)")
    private String internalNotes;

    @Size(max = 1000, message = "Compliance notes must not exceed 1000 characters")
    @Schema(description = "Compliance and regulatory notes")
    private String complianceNotes;

    // System Information
    @Size(max = 50, message = "Source system must not exceed 50 characters")
    @Schema(description = "Source system", example = "T24")
    private String sourceSystem;

    @Schema(description = "Record is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Record version for optimistic locking", example = "1")
    private Long version;

    // Audit Information
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp", example = "2024-01-15T09:30:00")
    private LocalDateTime createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last modification timestamp", example = "2024-01-20T14:45:00")
    private LocalDateTime lastModifiedDate;

    @Schema(description = "Created by user", example = "marie.hassan")
    private String createdBy;

    @Schema(description = "Last modified by user", example = "ahmed.benjelloun")
    private String lastModifiedBy;

    // Computed Fields (read-only)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Whether contract is currently active", example = "true")
    private Boolean currentlyActive;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Whether contract has expired", example = "false")
    private Boolean expired;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Whether contract is expiring soon", example = "true")
    private Boolean expiringSoon;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Days until expiration", example = "45")
    private Long daysUntilExpiration;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Contract duration in days", example = "365")
    private Long contractDurationDays;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Whether renewal reminder is needed", example = "true")
    private Boolean needsRenewalReminder;

    // Nested DTOs for detailed responses
    @Schema(description = "Number of associated documents", example = "3")
    private Integer documentCount;

    @Schema(description = "Contract type details")
    private ContractTypeDTO contractType;

    /**
     * Inner DTO for contract type information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Contract type information")
    public static class ContractTypeDTO {

        @Schema(description = "Contract type ID", example = "101")
        private Long id;

        @Schema(description = "Contract type code", example = "SOFTWARE_LICENSE")
        private String typeCode;

        @Schema(description = "Contract type name", example = "Software License Agreement")
        private String typeName;

        @Schema(description = "Contract type description")
        private String description;

        @Schema(description = "Contract category", example = "IT_SERVICES")
        private String category;

        @Schema(description = "Category display name", example = "IT & Technology Services")
        private String categoryDisplayName;

        @Schema(description = "Default duration in months", example = "12")
        private Integer defaultDurationMonths;

        @Schema(description = "Default reminder days", example = "30")
        private Integer defaultReminderDays;

        @Schema(description = "Requires approval", example = "true")
        private Boolean requiresApproval;

        @Schema(description = "Auto-renewal allowed", example = "true")
        private Boolean autoRenewalAllowed;

        @Schema(description = "Has financial impact", example = "true")
        private Boolean financialImpact;

        @Schema(description = "Risk category", example = "MEDIUM_RISK")
        private String riskCategory;
    }

    /**
     * Helper method to create a summary DTO with essential fields only
     */
    public static ContractDTO createSummary(Long id, String contractNumber, String title,
                                            Contract.ContractStatus status, String customerName,
                                            LocalDate endDate, BigDecimal contractValue, String currency) {
        return ContractDTO.builder()
                .id(id)
                .contractNumber(contractNumber)
                .title(title)
                .status(status)
                .statusDisplayName(status.getDisplayName())
                .customerName(customerName)
                .endDate(endDate)
                .contractValue(contractValue)
                .currency(currency)
                .formattedValue(formatValue(contractValue, currency))
                .build();
    }

    /**
     * Format currency value
     */
    private static String formatValue(BigDecimal value, String currency) {
        if (value == null) {
            return "N/A";
        }
        String currencyCode = currency != null ? currency : "MAD";
        return String.format("%s %,.2f", currencyCode, value);
    }

    /**
     * Check if this is a high-value contract (over 1 million MAD)
     */
    public boolean isHighValue() {
        if (contractValue == null) {
            return false;
        }
        return contractValue.compareTo(new BigDecimal("1000000")) >= 0;
    }

    /**
     * Get contract age in days since creation
     */
    public long getContractAgeDays() {
        if (createdDate == null) {
            return 0;
        }
        return createdDate.toLocalDate().until(LocalDate.now()).getDays();
    }
}