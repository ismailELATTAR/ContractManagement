package com.bp.contractrepository.shared.mapper;

import com.bp.contractrepository.contract.entity.ContractType;
import com.bp.contractrepository.contract.dto.ContractDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * ContractType Mapper using MapStruct
 * Handles conversion between ContractType entities and DTOs
 *
 * @author Contract Management Team
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ContractTypeMapper {

    // =====================================
    // Entity to DTO Mappings
    // =====================================

    /**
     * Convert ContractType entity to ContractTypeDTO
     */
    @Mapping(target = "categoryDisplayName", source = "category", qualifiedByName = "mapCategoryDisplayName")
    @Mapping(target = "riskCategory", source = "riskCategory", qualifiedByName = "mapRiskCategoryName")
    ContractDTO.ContractTypeDTO toDTO(ContractType contractType);

    /**
     * Convert list of ContractType entities to list of ContractTypeDTOs
     */
    List<ContractDTO.ContractTypeDTO> toDTOs(List<ContractType> contractTypes);

    /**
     * Convert ContractType to simple option DTO for dropdowns
     */
    @Mapping(target = "categoryDisplayName", source = "category", qualifiedByName = "mapCategoryDisplayName")
    @Mapping(target = "riskCategory", source = "riskCategory", qualifiedByName = "mapRiskCategoryName")
    // Only essential fields for dropdown/select lists
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "defaultDurationMonths", ignore = true)
    @Mapping(target = "defaultReminderDays", ignore = true)
    @Mapping(target = "requiresApproval", ignore = true)
    @Mapping(target = "autoRenewalAllowed", ignore = true)
    @Mapping(target = "financialImpact", ignore = true)
    ContractDTO.ContractTypeDTO toOptionDTO(ContractType contractType);

    // =====================================
    // Named Mapping Methods
    // =====================================

    /**
     * Map category enum to display name
     */
    @Named("mapCategoryDisplayName")
    default String mapCategoryDisplayName(ContractType.ContractCategory category) {
        return category != null ? category.getDisplayName() : null;
    }

    /**
     * Map risk category enum to string name
     */
    @Named("mapRiskCategoryName")
    default String mapRiskCategoryName(ContractType.RiskCategory riskCategory) {
        return riskCategory != null ? riskCategory.name() : null;
    }

    // =====================================
    // Utility Methods
    // =====================================

    /**
     * Create simple option for dropdown lists
     */
    default ContractTypeOption createOption(Long id, String typeCode, String typeName, String category) {
        if (id == null) {
            return null;
        }

        return ContractTypeOption.builder()
                .id(id)
                .typeCode(typeCode)
                .typeName(typeName)
                .category(category)
                .build();
    }

    /**
     * Simple DTO for dropdown options
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class ContractTypeOption {
        private Long id;
        private String typeCode;
        private String typeName;
        private String category;
    }
}