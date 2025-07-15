package com.bp.contractrepository.contract.entity;

import com.bp.contractrepository.shared.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Contract Entity - Core business entity for contract management
 * Represents a legal agreement between Banque Populaire and external parties
 *
 * @author Contract Management Team
 */
@Entity
@Table(name = "contracts", schema = "contract_mgmt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contract extends BaseEntity {

    @NotBlank(message = "Contract number is required")
    @Size(max = 50, message = "Contract number must not exceed 50 characters")
    @Column(name = "contract_number", length = 50, nullable = false, unique = true)
    private String contractNumber;

    @NotBlank(message = "Contract title is required")
    @Size(max = 200, message = "Contract title must not exceed 200 characters")
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull(message = "Contract type is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_type_id", nullable = false)
    private ContractType contractType;

    @NotNull(message = "Contract status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ContractStatus status;

    // Customer Information (from T24/Evolan)
    @NotBlank(message = "Customer ID is required")
    @Size(max = 50, message = "Customer ID must not exceed 50 characters")
    @Column(name = "customer_id", length = 50, nullable = false)
    private String customerId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 200, message = "Customer name must not exceed 200 characters")
    @Column(name = "customer_name", length = 200, nullable = false)
    private String customerName;

    @Size(max = 50, message = "Customer type must not exceed 50 characters")
    @Column(name = "customer_type", length = 50)
    private String customerType;

    // Contract Parties
    @NotBlank(message = "Internal department is required")
    @Size(max = 100, message = "Internal department must not exceed 100 characters")
    @Column(name = "internal_department", length = 100, nullable = false)
    private String internalDepartment;

    @NotBlank(message = "External party is required")
    @Size(max = 200, message = "External party must not exceed 200 characters")
    @Column(name = "external_party", length = 200, nullable = false)
    private String externalParty;

    // Contract Dates
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "renewal_date")
    private LocalDate renewalDate;

    // Financial Information
    @DecimalMin(value = "0.0", inclusive = false, message = "Contract value must be positive")
    @Digits(integer = 15, fraction = 2, message = "Contract value format is invalid")
    @Column(name = "contract_value", precision = 17, scale = 2)
    private BigDecimal contractValue;

    @Size(max = 3, message = "Currency must be 3 characters")
    @Column(name = "currency", length = 3)
    private String currency;

    @Size(max = 100, message = "Payment terms must not exceed 100 characters")
    @Column(name = "payment_terms", length = 100)
    private String paymentTerms;

    // Risk and Business Information
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private RiskLevel riskLevel;

    @Size(max = 100, message = "Business owner must not exceed 100 characters")
    @Column(name = "business_owner", length = 100)
    private String businessOwner;

    @Size(max = 100, message = "Primary contact must not exceed 100 characters")
    @Column(name = "primary_contact", length = 100)
    private String primaryContact;

    @Size(max = 100, message = "Relationship manager must not exceed 100 characters")
    @Column(name = "relationship_manager", length = 100)
    private String relationshipManager;

    // Additional Information
    @Column(name = "auto_renewal")
    private Boolean autoRenewal = false;

    @Min(value = 1, message = "Reminder days must be positive")
    @Max(value = 365, message = "Reminder days must not exceed 365")
    @Column(name = "reminder_days")
    private Integer reminderDays;

    @Size(max = 1000, message = "Internal notes must not exceed 1000 characters")
    @Column(name = "internal_notes", length = 1000)
    private String internalNotes;

    @Size(max = 1000, message = "Compliance notes must not exceed 1000 characters")
    @Column(name = "compliance_notes", length = 1000)
    private String complianceNotes;

    // T24/Evolan Integration Fields
    @Size(max = 50, message = "T24 customer ID must not exceed 50 characters")
    @Column(name = "t24_customer_id", length = 50)
    private String t24CustomerId;

    @Size(max = 50, message = "Source system must not exceed 50 characters")
    @Column(name = "source_system", length = 50)
    private String sourceSystem; // T24, EVOLAN, MANUAL

    /**
     * Contract Status Enumeration
     */
    public enum ContractStatus {
        DRAFT("Draft"),
        PENDING_APPROVAL("Pending Approval"),
        ACTIVE("Active"),
        SUSPENDED("Suspended"),
        EXPIRED("Expired"),
        TERMINATED("Terminated"),
        RENEWED("Renewed");

        private final String displayName;

        ContractStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Risk Level Enumeration
     */
    public enum RiskLevel {
        LOW("Low Risk"),
        MEDIUM("Medium Risk"),
        HIGH("High Risk"),
        CRITICAL("Critical Risk");

        private final String displayName;

        RiskLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Business logic methods
     */

    /**
     * Check if contract is currently active
     */
    public boolean isCurrentlyActive() {
        LocalDate now = LocalDate.now();
        return status == ContractStatus.ACTIVE &&
                !startDate.isAfter(now) &&
                !endDate.isBefore(now) &&
                Boolean.TRUE.equals(getIsActive());
    }


    public boolean isExpiringSoon(int days) {
        if (endDate == null) {
            return false;
        }
        LocalDate thresholdDate = LocalDate.now().plusDays(days);
        return endDate.isBefore(thresholdDate) || endDate.equals(thresholdDate);
    }


    public boolean hasExpired() {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    /**
     * Calculate contract duration in days
     */
    public long getContractDurationDays() {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return startDate.until(endDate).getDays();
    }

    /**
     * Get days until expiration
     */
    public long getDaysUntilExpiration() {
        if (endDate == null) {
            return Long.MAX_VALUE;
        }
        LocalDate now = LocalDate.now();
        return now.until(endDate).getDays();
    }

    /**
     * Check if contract needs renewal reminder
     */
    public boolean needsRenewalReminder() {
        if (reminderDays == null || endDate == null) {
            return false;
        }
        return getDaysUntilExpiration() <= reminderDays;
    }

    /**
     * Get contract value formatted as string
     */
    public String getFormattedContractValue() {
        if (contractValue == null) {
            return "N/A";
        }
        String currencyCode = currency != null ? currency : "MAD";
        return String.format("%s %.2f", currencyCode, contractValue);
    }

    /**
     * Pre-persist logic
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();

        // Set default values
        if (status == null) {
            status = ContractStatus.DRAFT;
        }
        if (currency == null) {
            currency = "MAD"; // Moroccan Dirham default
        }
        if (autoRenewal == null) {
            autoRenewal = false;
        }
        if (reminderDays == null) {
            reminderDays = 30; // Default 30 days reminder
        }
    }
}