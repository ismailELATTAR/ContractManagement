package com.bp.contractrepository.shared.mapper;

import com.bp.contractrepository.contract.dto.ContractDTO;
import com.bp.contractrepository.contract.dto.CreateContractRequest;
import com.bp.contractrepository.contract.dto.UpdateContractRequest;
import com.bp.contractrepository.contract.entity.Contract;
import com.bp.contractrepository.contract.entity.ContractType;
import org.mapstruct.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Contract Mapper using MapStruct
 * Handles conversion between Contract entities and DTOs
 *
 * @author Contract Management Team
 */
@Mapper(
        componentModel = "spring",
        uses = {ContractTypeMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ContractMapper {

    // =====================================
    // Entity to DTO Mappings
    // =====================================

    /**
     * Convert Contract entity to ContractDTO
     */
    @Mapping(target = "contractTypeId", source = "contractType.id")
    @Mapping(target = "contractTypeCode", source = "contractType.typeCode")
    @Mapping(target = "contractTypeName", source = "contractType.typeName")
    @Mapping(target = "contractTypeCategory", source = "contractType.category")
    @Mapping(target = "statusDisplayName", source = "status", qualifiedByName = "mapStatusDisplayName")
    @Mapping(target = "riskLevelDisplayName", source = "riskLevel", qualifiedByName = "mapRiskLevelDisplayName")
    @Mapping(target = "formattedValue", source = ".", qualifiedByName = "mapFormattedValue")
    @Mapping(target = "currentlyActive", source = ".", qualifiedByName = "mapCurrentlyActive")
    @Mapping(target = "expired", source = ".", qualifiedByName = "mapExpired")
    @Mapping(target = "expiringSoon", source = ".", qualifiedByName = "mapExpiringSoon")
    @Mapping(target = "daysUntilExpiration", source = ".", qualifiedByName = "mapDaysUntilExpiration")
    @Mapping(target = "contractDurationDays", source = ".", qualifiedByName = "mapContractDurationDays")
    @Mapping(target = "needsRenewalReminder", source = ".", qualifiedByName = "mapNeedsRenewalReminder")
    @Mapping(target = "documentCount", ignore = true) // Set separately if needed
    @Mapping(target = "contractType", source = "contractType")
    ContractDTO toDTO(Contract contract);

    /**
     * Convert list of Contract entities to list of ContractDTOs
     */
    List<ContractDTO> toDTOs(List<Contract> contracts);

    /**
     * Convert Contract entity to ContractSummaryDTO (lightweight)
     */
    @Mapping(target = "contractTypeId", source = "contractType.id")
    @Mapping(target = "contractTypeCode", source = "contractType.typeCode")
    @Mapping(target = "contractTypeName", source = "contractType.typeName")
    @Mapping(target = "statusDisplayName", source = "status", qualifiedByName = "mapStatusDisplayName")
    @Mapping(target = "formattedValue", source = ".", qualifiedByName = "mapFormattedValue")
    @Mapping(target = "currentlyActive", source = ".", qualifiedByName = "mapCurrentlyActive")
    @Mapping(target = "expired", source = ".", qualifiedByName = "mapExpired")
    @Mapping(target = "expiringSoon", source = ".", qualifiedByName = "mapExpiringSoon")
    @Mapping(target = "daysUntilExpiration", source = ".", qualifiedByName = "mapDaysUntilExpiration")
    // Ignore detailed fields for summary
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "internalNotes", ignore = true)
    @Mapping(target = "complianceNotes", ignore = true)
    @Mapping(target = "contractType", ignore = true)
    @Mapping(target = "documentCount", ignore = true)
    @Mapping(target = "contractDurationDays", ignore = true)
    @Mapping(target = "needsRenewalReminder", ignore = true)
    @Mapping(target = "contractTypeCategory", ignore = true)
    @Mapping(target = "riskLevelDisplayName", ignore = true)
    ContractDTO toSummaryDTO(Contract contract);

    // =====================================
    // DTO to Entity Mappings
    // =====================================

    /**
     * Convert CreateContractRequest to Contract entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contractType", ignore = true) // Set separately
    @Mapping(target = "status", constant = "DRAFT")
    @Mapping(target = "customerName", ignore = true) // Set from customer service
    @Mapping(target = "customerType", ignore = true) // Set from customer service
    @Mapping(target = "t24CustomerId", ignore = true) // Set from customer service
    @Mapping(target = "relationshipManager", ignore = true) // Set from customer service
    @Mapping(target = "sourceSystem", ignore = true) // Set from customer service
    @Mapping(target = "currency", source = "currency", defaultValue = "MAD")
    @Mapping(target = "autoRenewal", source = "autoRenewal", defaultValue = "false")
    @Mapping(target = "reminderDays", source = "reminderDays", defaultValue = "30")
    // Ignore audit fields - handled by BaseEntity
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Contract toEntity(CreateContractRequest request);

    /**
     * Update Contract entity from UpdateContractRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "contractNumber", ignore = true) // Never update contract number
    @Mapping(target = "contractType", ignore = true) // Set separately if contractTypeId provided
    @Mapping(target = "status", ignore = true) // Status changes via separate methods
    @Mapping(target = "customerId", ignore = true) // Customer never changes
    @Mapping(target = "customerName", ignore = true) // Managed by sync
    @Mapping(target = "customerType", ignore = true) // Managed by sync
    @Mapping(target = "t24CustomerId", ignore = true) // Managed by sync
    @Mapping(target = "internalDepartment", ignore = true) // Rarely changes
    @Mapping(target = "externalParty", ignore = true) // Rarely changes
    @Mapping(target = "relationshipManager", ignore = true) // Managed by sync
    @Mapping(target = "sourceSystem", ignore = true) // Never changes
    // Ignore audit fields - handled by BaseEntity
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntityFromDTO(UpdateContractRequest request, @MappingTarget Contract contract);

    // =====================================
    // Named Mapping Methods
    // =====================================

    /**
     * Map contract status to display name
     */
    @Named("mapStatusDisplayName")
    default String mapStatusDisplayName(Contract.ContractStatus status) {
        return status != null ? status.getDisplayName() : null;
    }

    /**
     * Map risk level to display name
     */
    @Named("mapRiskLevelDisplayName")
    default String mapRiskLevelDisplayName(Contract.RiskLevel riskLevel) {
        return riskLevel != null ? riskLevel.getDisplayName() : null;
    }

    /**
     * Format contract value with currency
     */
    @Named("mapFormattedValue")
    default String mapFormattedValue(Contract contract) {
        if (contract.getContractValue() == null) {
            return "N/A";
        }
        String currency = contract.getCurrency() != null ? contract.getCurrency() : "MAD";
        return String.format("%s %,.2f", currency, contract.getContractValue());
    }

    /**
     * Check if contract is currently active
     */
    @Named("mapCurrentlyActive")
    default Boolean mapCurrentlyActive(Contract contract) {
        return contract.isCurrentlyActive();
    }

    /**
     * Check if contract has expired
     */
    @Named("mapExpired")
    default Boolean mapExpired(Contract contract) {
        return contract.hasExpired();
    }

    /**
     * Check if contract is expiring soon (within 30 days)
     */
    @Named("mapExpiringSoon")
    default Boolean mapExpiringSoon(Contract contract) {
        return contract.isExpiringSoon(30);
    }

    /**
     * Calculate days until expiration
     */
    @Named("mapDaysUntilExpiration")
    default Long mapDaysUntilExpiration(Contract contract) {
        return contract.getDaysUntilExpiration();
    }

    /**
     * Calculate contract duration in days
     */
    @Named("mapContractDurationDays")
    default Long mapContractDurationDays(Contract contract) {
        return contract.getContractDurationDays();
    }

    /**
     * Check if contract needs renewal reminder
     */
    @Named("mapNeedsRenewalReminder")
    default Boolean mapNeedsRenewalReminder(Contract contract) {
        return contract.needsRenewalReminder();
    }

    // =====================================
    // Helper Mapping Methods
    // =====================================

    /**
     * Map ContractType entity to ContractDTO.ContractTypeDTO
     */
    @Mapping(target = "categoryDisplayName", source = "category", qualifiedByName = "mapCategoryDisplayName")
    @Mapping(target = "riskCategory", source = "riskCategory", qualifiedByName = "mapRiskCategoryName")
    ContractDTO.ContractTypeDTO toContractTypeDTO(ContractType contractType);

    /**
     * Map category to display name
     */
    @Named("mapCategoryDisplayName")
    default String mapCategoryDisplayName(ContractType.ContractCategory category) {
        return category != null ? category.getDisplayName() : null;
    }

    /**
     * Map risk category to string
     */
    @Named("mapRiskCategoryName")
    default String mapRiskCategoryName(ContractType.RiskCategory riskCategory) {
        return riskCategory != null ? riskCategory.name() : null;
    }

    // =====================================
    // Utility Methods
    // =====================================

    /**
     * Create summary DTO for lists and search results
     */
    default ContractDTO createSummary(Long id, String contractNumber, String title,
                                      Contract.ContractStatus status, String customerName,
                                      LocalDate endDate, BigDecimal contractValue, String currency) {
        if (id == null) {
            return null;
        }

        return ContractDTO.builder()
                .id(id)
                .contractNumber(contractNumber)
                .title(title)
                .status(status)
                .statusDisplayName(status != null ? status.getDisplayName() : null)
                .customerName(customerName)
                .endDate(endDate)
                .contractValue(contractValue)
                .currency(currency)
                .formattedValue(formatValue(contractValue, currency))
                .currentlyActive(isActiveStatus(status, endDate))
                .expired(isExpired(endDate))
                .expiringSoon(isExpiringSoon(endDate))
                .daysUntilExpiration(calculateDaysUntilExpiration(endDate))
                .build();
    }

    /**
     * Format value helper
     */
    default String formatValue(BigDecimal value, String currency) {
        if (value == null) {
            return "N/A";
        }
        String currencyCode = currency != null ? currency : "MAD";
        return String.format("%s %,.2f", currencyCode, value);
    }

    /**
     * Check if status and dates indicate active contract
     */
    default Boolean isActiveStatus(Contract.ContractStatus status, LocalDate endDate) {
        if (status != Contract.ContractStatus.ACTIVE || endDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return !endDate.isBefore(now);
    }

    /**
     * Check if contract is expired
     */
    default Boolean isExpired(LocalDate endDate) {
        return endDate != null && endDate.isBefore(LocalDate.now());
    }

    /**
     * Check if contract is expiring soon
     */
    default Boolean isExpiringSoon(LocalDate endDate) {
        if (endDate == null) {
            return false;
        }
        LocalDate threshold = LocalDate.now().plusDays(30);
        return endDate.isBefore(threshold) || endDate.equals(threshold);
    }

    /**
     * Calculate days until expiration
     */
    default Long calculateDaysUntilExpiration(LocalDate endDate) {
        if (endDate == null) {
            return Long.MAX_VALUE;
        }
        LocalDate now = LocalDate.now();
        return (long) now.until(endDate).getDays();
    }
}