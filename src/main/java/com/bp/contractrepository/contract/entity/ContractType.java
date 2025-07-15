package com.bp.contractrepository.contract.entity;

import com.bp.contractrepository.shared.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Set;

/**
 * ContractType Entity - Master data for contract classification
 * Defines different types of contracts used by Banque Populaire
 *
 * Examples: Software License, Service Agreement, Vendor Contract, etc.
 *
 * @author Contract Management Team
 */
@Entity
@Table(name = "contract_types", schema = "contract_mgmt")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ContractType extends BaseEntity {

    @NotBlank(message = "Type code is required")
    @Size(max = 20, message = "Type code must not exceed 20 characters")
    @Pattern(regexp = "^[A-Z_]+$", message = "Type code must contain only uppercase letters and underscores")
    @Column(name = "type_code", length = 20, nullable = false, unique = true)
    private String typeCode;


    @NotBlank(message = "Type name is required")
    @Size(max = 100, message = "Type name must not exceed 100 characters")
    @Column(name = "type_name", length = 100, nullable = false)
    private String typeName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 30, nullable = false)
    private ContractCategory category;

    @Min(value = 1, message = "Default duration must be at least 1 month")
    @Max(value = 1200, message = "Default duration must not exceed 1200 months (100 years)")
    @Column(name = "default_duration_months")
    private Integer defaultDurationMonths;

    @Min(value = 1, message = "Default reminder days must be at least 1 day")
    @Max(value = 365, message = "Default reminder days must not exceed 365 days")
    @Column(name = "default_reminder_days")
    private Integer defaultReminderDays;

    @Column(name = "requires_approval")
    private Boolean requiresApproval = false;

    @Column(name = "auto_renewal_allowed")
    private Boolean autoRenewalAllowed = false;

    @Column(name = "financial_impact")
    private Boolean financialImpact = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", length = 20)
    private RiskCategory riskCategory;

    @Size(max = 200, message = "Approval workflow must not exceed 200 characters")
    @Column(name = "approval_workflow", length = 200)
    private String approvalWorkflow;

    @Size(max = 500, message = "Required documents must not exceed 500 characters")
    @Column(name = "required_documents", length = 500)
    private String requiredDocuments;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @OneToMany(mappedBy = "contractType", fetch = FetchType.LAZY)
    private Set<Contract> contracts;

    /**
     * Contract Category Enumeration
     */
    public enum ContractCategory {
        IT_SERVICES("IT & Technology Services"),
        VENDOR_SERVICES("Vendor & Supplier Services"),
        PROFESSIONAL_SERVICES("Professional Services"),
        FACILITY_MANAGEMENT("Facility Management"),
        BANKING_SERVICES("Banking & Financial Services"),
        LEGAL_SERVICES("Legal Services"),
        HUMAN_RESOURCES("Human Resources"),
        MARKETING_SERVICES("Marketing & Communications"),
        INSURANCE("Insurance & Risk Management"),
        REAL_ESTATE("Real Estate & Property"),
        OTHER("Other Services");

        private final String displayName;

        ContractCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    /**
     * Risk Category Enumeration
     */
    public enum RiskCategory {
        LOW_RISK("Low Risk"),
        MEDIUM_RISK("Medium Risk"),
        HIGH_RISK("High Risk"),
        CRITICAL_RISK("Critical Risk");

        private final String displayName;

        RiskCategory(String displayName) {
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
     * Check if this contract type requires approval workflow
     */
    public boolean needsApproval() {
        return Boolean.TRUE.equals(requiresApproval);
    }

    /**
     * Check if auto-renewal is allowed for this contract type
     */
    public boolean isAutoRenewalAllowed() {
        return Boolean.TRUE.equals(autoRenewalAllowed);
    }

    /**
     * Check if this contract type has financial impact
     */
    public boolean hasFinancialImpact() {
        return Boolean.TRUE.equals(financialImpact);
    }

    /**
     * Get the default reminder days or fallback to 30
     */
    public int getEffectiveReminderDays() {
        return defaultReminderDays != null ? defaultReminderDays : 30;
    }

    /**
     * Get the default duration or fallback to 12 months
     */
    public int getEffectiveDurationMonths() {
        return defaultDurationMonths != null ? defaultDurationMonths : 12;
    }

    /**
     * Check if this is a high-risk contract type
     */
    public boolean isHighRisk() {
        return riskCategory == RiskCategory.HIGH_RISK ||
                riskCategory == RiskCategory.CRITICAL_RISK;
    }

    /**
     * Get formatted display name with category
     */
    public String getFullDisplayName() {
        return String.format("%s (%s)", typeName, category.getDisplayName());
    }

    /**
     * Count active contracts of this type
     */
    public long getActiveContractCount() {
        if (contracts == null) {
            return 0;
        }
        return contracts.stream()
                .filter(contract -> Boolean.TRUE.equals(contract.getIsActive()))
                .count();
    }

    /**
     * Pre-persist logic
     */
    @PrePersist
    protected void onCreate() {
        super.onCreate();

        if (requiresApproval == null) {
            requiresApproval = false;
        }
        if (autoRenewalAllowed == null) {
            autoRenewalAllowed = false;
        }
        if (financialImpact == null) {
            financialImpact = false;
        }
        if (displayOrder == null) {
            displayOrder = 0;
        }
        if (defaultReminderDays == null) {
            defaultReminderDays = 30;
        }
        if (defaultDurationMonths == null) {
            defaultDurationMonths = 12;
        }

        // Auto-assign risk category based on category if not set
        if (riskCategory == null) {
            riskCategory = getDefaultRiskCategoryForType();
        }
    }

    /**
     * Get default risk category based on contract category
     */
    private RiskCategory getDefaultRiskCategoryForType() {
        return switch (category) {
            case IT_SERVICES, BANKING_SERVICES -> RiskCategory.HIGH_RISK;
            case PROFESSIONAL_SERVICES, LEGAL_SERVICES -> RiskCategory.MEDIUM_RISK;
            case FACILITY_MANAGEMENT, MARKETING_SERVICES -> RiskCategory.LOW_RISK;
            case INSURANCE -> RiskCategory.CRITICAL_RISK;
            default -> RiskCategory.MEDIUM_RISK;
        };
    }

    /**
     * Static method to create common contract types
     */
    public static ContractType createSoftwareLicense() {
        return ContractType.builder()
                .typeCode("SOFTWARE_LICENSE")
                .typeName("Software License Agreement")
                .description("Software licensing and subscription agreements")
                .category(ContractCategory.IT_SERVICES)
                .defaultDurationMonths(12)
                .defaultReminderDays(60)
                .requiresApproval(true)
                .autoRenewalAllowed(true)
                .financialImpact(true)
                .riskCategory(RiskCategory.MEDIUM_RISK)
                .build();
    }

    public static ContractType createVendorAgreement() {
        return ContractType.builder()
                .typeCode("VENDOR_AGREEMENT")
                .typeName("Vendor Service Agreement")
                .description("General vendor and supplier service agreements")
                .category(ContractCategory.VENDOR_SERVICES)
                .defaultDurationMonths(24)
                .defaultReminderDays(90)
                .requiresApproval(true)
                .autoRenewalAllowed(false)
                .financialImpact(true)
                .riskCategory(RiskCategory.MEDIUM_RISK)
                .build();
    }

    public static ContractType createBankingService() {
        return ContractType.builder()
                .typeCode("BANKING_SERVICE")
                .typeName("Banking Service Agreement")
                .description("Core banking and financial service contracts")
                .category(ContractCategory.BANKING_SERVICES)
                .defaultDurationMonths(36)
                .defaultReminderDays(120)
                .requiresApproval(true)
                .autoRenewalAllowed(false)
                .financialImpact(true)
                .riskCategory(RiskCategory.CRITICAL_RISK)
                .build();
    }
}