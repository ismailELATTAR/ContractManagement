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
 * Create Contract Request DTO
 * Used for creating new contracts via API
 *
 * @author Contract Management Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new contract")
public class CreateContractRequest {

    @Size(max = 50, message = "Contract number must not exceed 50 characters")
    @Schema(description = "Contract number (auto-generated if not provided)", example = "BP-2024-SW-001")
    private String contractNumber;

    @NotBlank(message = "Contract title is required")
    @Size(max = 200, message = "Contract title must not exceed 200 characters")
    @Schema(description = "Contract title", example = "Microsoft Office 365 License Agreement", required = true)
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Schema(description = "Detailed contract description", example = "Annual subscription for Microsoft Office 365 Enterprise licenses for 500 users")
    private String description;

    @NotNull(message = "Contract type ID is required")
    @Schema(description = "Contract type identifier", example = "101", required = true)
    private Long contractTypeId;

    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    @Schema(description = "Customer identifier from core banking", example = "CUS-12345", required = true)
    private String customerId;

    @NotBlank(message = "Internal department is required")
    @Size(max = 100, message = "Internal department must not exceed 100 characters")
    @Schema(description = "Internal department responsible", example = "IT Department", required = true)
    private String internalDepartment;

    @NotBlank(message = "External party is required")
    @Size(max = 200, message = "External party must not exceed 200 characters")
    @Schema(description = "External contracting party", example = "Microsoft Maroc SARL", required = true)
    private String externalParty;

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

    @DecimalMin(value = "0.0", inclusive = false, message = "Contract value must be positive")
    @Digits(integer = 15, fraction = 2, message = "Contract value format is invalid")
    @Schema(description = "Contract monetary value", example = "500000.00")
    private BigDecimal contractValue;

    @Size(max = 3, message = "Currency must be 3 characters")
    @Schema(description = "Contract currency (defaults to MAD)", example = "MAD")
    private String currency;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Schema(description = "Payment terms", example = "Annual payment in advance")
    private String paymentTerms;

    @Schema(description = "Contract risk level", example = "MEDIUM")
    private Contract.RiskLevel riskLevel;

    @Size(max = 100, message = "Business owner must not exceed 100 characters")
    @Schema(description = "Business owner responsible for contract", example = "Ahmed Benjelloun")
    private String businessOwner;

    @Size(max = 100, message = "Primary contact must not exceed 100 characters")
    @Schema(description = "Primary contact person", example = "Fatima El Alami")
    private String primaryContact;

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

    /**
     * Validation method to check if start date is before end date
     */
    @AssertTrue(message = "Start date must be before end date")
    public boolean isValidDateRange() {
        if (startDate == null || endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return startDate.isBefore(endDate);
    }

    /**
     * Validation method to check if end date is not in the past
     */
    @AssertTrue(message = "End date cannot be in the past")
    public boolean isEndDateNotInPast() {
        if (endDate == null) {
            return true; // Let @NotNull handle null validation
        }
        return !endDate.isBefore(LocalDate.now());
    }

    /**
     * Validation method to check if renewal date is between start and end dates
     */
    @AssertTrue(message = "Renewal date must be between start and end dates")
    public boolean isValidRenewalDate() {
        if (renewalDate == null || startDate == null || endDate == null) {
            return true; // Renewal date is optional
        }
        return !renewalDate.isBefore(startDate) && !renewalDate.isAfter(endDate);
    }

    /**
     * Helper method to get contract value or zero if null
     */
    public BigDecimal getContractValueOrZero() {
        return contractValue != null ? contractValue : BigDecimal.ZERO;
    }

    /**
     * Helper method to get currency or MAD default
     */
    public String getCurrencyOrDefault() {
        return currency != null ? currency : "MAD";
    }

    /**
     * Helper method to check if this is a high-value contract (over 1M MAD)
     */
    public boolean isHighValue() {
        if (contractValue == null) {
            return false;
        }
        return contractValue.compareTo(new BigDecimal("1000000")) >= 0;
    }

    /**
     * Helper method to get contract duration in days
     */
    public long getContractDurationDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return startDate.until(endDate).getDays();
    }

    /**
     * Helper method to check if contract duration is over a year
     */
    public boolean isLongTermContract() {
        return getContractDurationDays() > 365;
    }
}