package com.bp.contractrepository.contract.dto;

import com.bp.contractrepository.contract.entity.Contract;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Update Contract Request DTO
 * Used for updating existing contracts via API
 * All fields are optional - only provided fields will be updated
 *
 * @author Contract Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update an existing contract")
public class UpdateContractRequest {

    @Size(max = 200, message = "Contract title must not exceed 200 characters")
    @Schema(description = "Contract title", example = "Microsoft Office 365 License Agreement (Updated)")
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Detailed contract description", example = "Updated annual subscription for Microsoft Office 365 Enterprise licenses for 750 users")
    private String description;

    @Schema(description = "Contract type identifier", example = "102")
    private Long contractTypeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Contract start date", example = "2024-02-01")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Contract end date", example = "2025-01-31")
    private LocalDate endDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Contract renewal date", example = "2024-12-01")
    private LocalDate renewalDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "Contract value must be positive")
    @Digits(integer = 15, fraction = 2, message = "Contract value format is invalid")
    @Schema(description = "Contract monetary value", example = "750000.00")
    private BigDecimal contractValue;

    @Size(max = 3, message = "Currency must be 3 characters")
    @Schema(description = "Contract currency", example = "MAD")
    private String currency;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Schema(description = "Payment terms", example = "Quarterly payment in advance")
    private String paymentTerms;

    @Schema(description = "Contract risk level", example = "HIGH")
    private Contract.RiskLevel riskLevel;

    @Size(max = 100, message = "Business owner must not exceed 100 characters")
    @Schema(description = "Business owner responsible for contract", example = "Youssef Sekkouri")
    private String businessOwner;

    @Size(max = 100, message = "Primary contact must not exceed 100 characters")
    @Schema(description = "Primary contact person", example = "Rachid Ouali")
    private String primaryContact;

    @Schema(description = "Auto-renewal enabled", example = "false")
    private Boolean autoRenewal;

    @Min(value = 1, message = "Reminder days must be positive")
    @Max(value = 365, message = "Reminder days must not exceed 365")
    @Schema(description = "Reminder days before expiration", example = "60")
    private Integer reminderDays;

    @Size(max = 1000, message = "Internal notes must not exceed 1000 characters")
    @Schema(description = "Internal notes (not visible to external parties)")
    private String internalNotes;

    @Size(max = 1000, message = "Compliance notes must not exceed 1000 characters")
    @Schema(description = "Compliance and regulatory notes")
    private String complianceNotes;

    /**
     * Validation method to check if start date is before end date (if both provided)
     */
    @AssertTrue(message = "Start date must be before end date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Only validate if both dates are provided
        }
        return startDate.isBefore(endDate);
    }

    /**
     * Validation method to check if end date is not in the past (if provided)
     */
    @AssertTrue(message = "End date cannot be in the past")
    public boolean isEndDateNotInPast() {
        if (endDate == null) {
            return true; // Only validate if end date is provided
        }
        return !endDate.isBefore(LocalDate.now());
    }

    /**
     * Validation method to check if renewal date is valid (if provided with other dates)
     */
    @AssertTrue(message = "Renewal date must be between start and end dates")
    public boolean isValidRenewalDate() {
        if (renewalDate == null) {
            return true; // Renewal date is optional
        }

        // If start and end dates are provided, validate renewal date is between them
        if (startDate != null && endDate != null) {
            return !renewalDate.isBefore(startDate) && !renewalDate.isAfter(endDate);
        }

        return true; // Can't validate without start/end dates
    }

    /**
     * Check if any field is provided for update
     */
    public boolean hasAnyFieldToUpdate() {
        return title != null ||
                description != null ||
                contractTypeId != null ||
                startDate != null ||
                endDate != null ||
                renewalDate != null ||
                contractValue != null ||
                currency != null ||
                paymentTerms != null ||
                riskLevel != null ||
                businessOwner != null ||
                primaryContact != null ||
                autoRenewal != null ||
                reminderDays != null ||
                internalNotes != null ||
                complianceNotes != null;
    }

    /**
     * Check if dates are being updated
     */
    public boolean isDatesUpdate() {
        return startDate != null || endDate != null || renewalDate != null;
    }

    /**
     * Check if financial information is being updated
     */
    public boolean isFinancialUpdate() {
        return contractValue != null || currency != null || paymentTerms != null;
    }

    /**
     * Check if risk or compliance information is being updated
     */
    public boolean isRiskOrComplianceUpdate() {
        return riskLevel != null || complianceNotes != null;
    }

    /**
     * Check if contact information is being updated
     */
    public boolean isContactUpdate() {
        return businessOwner != null || primaryContact != null;
    }

    /**
     * Check if lifecycle settings are being updated
     */
    public boolean isLifecycleUpdate() {
        return autoRenewal != null || reminderDays != null;
    }

    /**
     * Helper method to check if this update includes high-value contract (over 1M MAD)
     */
    public boolean isHighValueUpdate() {
        if (contractValue == null) {
            return false;
        }
        return contractValue.compareTo(new BigDecimal("1000000")) >= 0;
    }

    /**
     * Helper method to get contract duration in days (if both dates provided)
     */
    public long getContractDurationDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return startDate.until(endDate).getDays();
    }

    /**
     * Get list of fields being updated (for audit trail)
     */
    public java.util.List<String> getUpdatedFields() {
        java.util.List<String> updatedFields = new java.util.ArrayList<>();

        if (title != null) updatedFields.add("title");
        if (description != null) updatedFields.add("description");
        if (contractTypeId != null) updatedFields.add("contractTypeId");
        if (startDate != null) updatedFields.add("startDate");
        if (endDate != null) updatedFields.add("endDate");
        if (renewalDate != null) updatedFields.add("renewalDate");
        if (contractValue != null) updatedFields.add("contractValue");
        if (currency != null) updatedFields.add("currency");
        if (paymentTerms != null) updatedFields.add("paymentTerms");
        if (riskLevel != null) updatedFields.add("riskLevel");
        if (businessOwner != null) updatedFields.add("businessOwner");
        if (primaryContact != null) updatedFields.add("primaryContact");
        if (autoRenewal != null) updatedFields.add("autoRenewal");
        if (reminderDays != null) updatedFields.add("reminderDays");
        if (internalNotes != null) updatedFields.add("internalNotes");
        if (complianceNotes != null) updatedFields.add("complianceNotes");

        return updatedFields;
    }

    /**
     * Create a summary of the update for logging
     */
    public String getUpdateSummary() {
        java.util.List<String> fields = getUpdatedFields();
        if (fields.isEmpty()) {
            return "No fields to update";
        }
        return "Updating fields: " + String.join(", ", fields);
    }
}