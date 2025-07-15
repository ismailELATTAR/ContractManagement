package com.bp.contractrepository.contract.repository;

import com.bp.contractrepository.contract.entity.ContractType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ContractType Repository - Database access layer for ContractType entities
 * Provides CRUD operations and custom queries for contract type management
 *
 * @author Contract Management Team
 */
@Repository
public interface ContractTypeRepository extends JpaRepository<ContractType, Long> {

    // =====================================
    // Basic Finder Methods
    // =====================================

    /**
     * Find contract type by unique type code
     */
    Optional<ContractType> findByTypeCodeAndIsActiveTrue(String typeCode);

    /**
     * Find contract type by type code (including inactive)
     */
    Optional<ContractType> findByTypeCode(String typeCode);

    /**
     * Check if type code already exists
     */
    boolean existsByTypeCode(String typeCode);

    /**
     * Find all active contract types
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    List<ContractType> findAllActive();

    /**
     * Find all active contract types with pagination
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    Page<ContractType> findAllActive(Pageable pageable);

    /**
     * Find contract type by name (case-insensitive)
     */
    @Query("SELECT ct FROM ContractType ct WHERE LOWER(ct.typeName) = LOWER(:typeName) AND ct.isActive = true")
    Optional<ContractType> findByTypeNameIgnoreCaseAndIsActiveTrue(@Param("typeName") String typeName);

    // =====================================
    // Category-based Queries
    // =====================================

    /**
     * Find contract types by category
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.category = :category AND ct.isActive = true ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    List<ContractType> findByCategoryAndIsActiveTrue(@Param("category") ContractType.ContractCategory category);

    /**
     * Find contract types by category with pagination
     */

    @Query("SELECT ct FROM ContractType ct WHERE ct.category = :category AND ct.isActive = true ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    Page<ContractType> findByCategoryAndIsActiveTrue(@Param("category") ContractType.ContractCategory category, Pageable pageable);

    /**
     * Get contract type counts by category
     */
    @Query("SELECT ct.category, COUNT(ct) FROM ContractType ct WHERE ct.isActive = true GROUP BY ct.category ORDER BY COUNT(ct) DESC")
    List<Object[]> getContractTypeCountsByCategory();

    // =====================================
    // Risk and Approval Queries
    // =====================================

    /**
     * Find contract types that require approval
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.requiresApproval = true AND ct.isActive = true ORDER BY ct.typeName ASC")
    List<ContractType> findTypesRequiringApproval();

    /**
     * Find contract types by risk category
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.riskCategory = :riskCategory AND ct.isActive = true ORDER BY ct.typeName ASC")
    List<ContractType> findByRiskCategoryAndIsActiveTrue(@Param("riskCategory") ContractType.RiskCategory riskCategory);

    /**
     * Find high-risk contract types
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.riskCategory IN ('HIGH_RISK', 'CRITICAL_RISK') AND ct.isActive = true ORDER BY ct.riskCategory DESC, ct.typeName ASC")
    List<ContractType> findHighRiskTypes();

    /**
     * Find contract types allowing auto-renewal
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.autoRenewalAllowed = true AND ct.isActive = true ORDER BY ct.typeName ASC")
    List<ContractType> findTypesAllowingAutoRenewal();

    /**
     * Find contract types with financial impact
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.financialImpact = true AND ct.isActive = true ORDER BY ct.typeName ASC")
    List<ContractType> findTypesWithFinancialImpact();

    // =====================================
    // Search and Filter Queries
    // =====================================

    /**
     * Search contract types by name or description
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true AND (" +
            "LOWER(ct.typeName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(ct.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            ") ORDER BY ct.typeName ASC")
    List<ContractType> searchContractTypes(@Param("searchTerm") String searchTerm);

    /**
     * Search contract types by name or description with pagination
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true AND (" +
            "LOWER(ct.typeName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(ct.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            ") ORDER BY ct.typeName ASC")
    Page<ContractType> searchContractTypes(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true " +
            "AND (:category IS NULL OR ct.category = :category) " +
            "AND (:riskCategory IS NULL OR ct.riskCategory = :riskCategory) " +
            "AND (:requiresApproval IS NULL OR ct.requiresApproval = :requiresApproval) " +
            "AND (:autoRenewalAllowed IS NULL OR ct.autoRenewalAllowed = :autoRenewalAllowed) " +
            "AND (:financialImpact IS NULL OR ct.financialImpact = :financialImpact) " +
            "ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    Page<ContractType> findWithCriteria(
            @Param("category") ContractType.ContractCategory category,
            @Param("riskCategory") ContractType.RiskCategory riskCategory,
            @Param("requiresApproval") Boolean requiresApproval,
            @Param("autoRenewalAllowed") Boolean autoRenewalAllowed,
            @Param("financialImpact") Boolean financialImpact,
            Pageable pageable);

    // =====================================
    // Configuration Queries
    // =====================================

    /**
     * Find contract types with default duration in range
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.defaultDurationMonths BETWEEN :minMonths AND :maxMonths AND ct.isActive = true ORDER BY ct.defaultDurationMonths ASC")
    List<ContractType> findByDefaultDurationRange(@Param("minMonths") Integer minMonths, @Param("maxMonths") Integer maxMonths);

    /**
     * Find contract types with specific reminder days
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.defaultReminderDays = :reminderDays AND ct.isActive = true ORDER BY ct.typeName ASC")
    List<ContractType> findByDefaultReminderDays(@Param("reminderDays") Integer reminderDays);

    /**
     * Find contract types with specific approval workflow
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.approvalWorkflow = :workflow AND ct.isActive = true ORDER BY ct.typeName ASC")
    List<ContractType> findByApprovalWorkflow(@Param("workflow") String workflow);

    // =====================================
    // Usage Statistics Queries
    // =====================================

    /**
     * Get contract types with their usage count
     */
    @Query("SELECT ct, COUNT(c) FROM ContractType ct " +
            "LEFT JOIN Contract c ON c.contractType = ct AND c.isActive = true " +
            "WHERE ct.isActive = true " +
            "GROUP BY ct " +
            "ORDER BY COUNT(c) DESC, ct.typeName ASC")
    List<Object[]> findContractTypesWithUsageCount();

    /**
     * Get most used contract types (top N)
     */
    @Query("SELECT ct FROM ContractType ct " +
            "LEFT JOIN Contract c ON c.contractType = ct AND c.isActive = true " +
            "WHERE ct.isActive = true " +
            "GROUP BY ct " +
            "ORDER BY COUNT(c) DESC")
    List<ContractType> findMostUsedContractTypes(Pageable pageable);

    /**
     * Get unused contract types (no active contracts)
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true AND " +
            "NOT EXISTS (SELECT 1 FROM Contract c WHERE c.contractType = ct AND c.isActive = true) " +
            "ORDER BY ct.typeName ASC")
    List<ContractType> findUnusedContractTypes();

    /**
     * Count active contracts by contract type
     */
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.id = :contractTypeId AND c.isActive = true")
    long countActiveContractsByType(@Param("contractTypeId") Long contractTypeId);

    // =====================================
    // Maintenance Queries
    // =====================================

    /**
     * Find contract types needing review (old or without recent usage)
     */
    @Query("SELECT ct FROM ContractType ct WHERE ct.isActive = true AND (" +
            "ct.lastModifiedDate < :reviewDate OR " +
            "NOT EXISTS (SELECT 1 FROM Contract c WHERE c.contractType = ct AND c.createdDate >= :reviewDate)" +
            ") ORDER BY ct.lastModifiedDate ASC")
    List<ContractType> findContractTypesNeedingReview(@Param("reviewDate") java.time.LocalDate reviewDate);

    /**
     * Find duplicate contract type names (case-insensitive)
     */
    @Query("SELECT ct1 FROM ContractType ct1, ContractType ct2 WHERE " +
            "ct1.id < ct2.id AND " +
            "LOWER(ct1.typeName) = LOWER(ct2.typeName) AND " +
            "ct1.isActive = true AND ct2.isActive = true")
    List<ContractType> findDuplicateTypeNames();

    // =====================================
    // Reporting Queries
    // =====================================

    /**
     * Get contract type statistics summary
     */
    @Query("SELECT " +
            "COUNT(ct) as totalTypes, " +
            "COUNT(CASE WHEN ct.requiresApproval = true THEN 1 END) as typesRequiringApproval, " +
            "COUNT(CASE WHEN ct.autoRenewalAllowed = true THEN 1 END) as typesAllowingAutoRenewal, " +
            "COUNT(CASE WHEN ct.financialImpact = true THEN 1 END) as typesWithFinancialImpact, " +
            "COUNT(CASE WHEN ct.riskCategory IN ('HIGH_RISK', 'CRITICAL_RISK') THEN 1 END) as highRiskTypes " +
            "FROM ContractType ct WHERE ct.isActive = true")
    Object[] getContractTypeStatistics();

    /**
     * Get contract types by risk category count
     */
    @Query("SELECT ct.riskCategory, COUNT(ct) FROM ContractType ct WHERE ct.isActive = true GROUP BY ct.riskCategory ORDER BY COUNT(ct) DESC")
    List<Object[]> getContractTypeCountsByRiskCategory();

    /**
     * Get average default duration by category
     */
    @Query("SELECT ct.category, AVG(ct.defaultDurationMonths) FROM ContractType ct WHERE ct.isActive = true AND ct.defaultDurationMonths IS NOT NULL GROUP BY ct.category")
    List<Object[]> getAverageDefaultDurationByCategory();

    // =====================================
    // Quick Lookup Methods
    // =====================================

    /**
     * Find contract types for dropdown/select lists
     */
    @Query("SELECT new map(ct.id as id, ct.typeName as name, ct.typeCode as code) " +
            "FROM ContractType ct WHERE ct.isActive = true ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    List<java.util.Map<String, Object>> findContractTypeOptions();

    /**
     * Find contract types by category for dropdown
     */
    @Query("SELECT new map(ct.id as id, ct.typeName as name, ct.typeCode as code) " +
            "FROM ContractType ct WHERE ct.category = :category AND ct.isActive = true ORDER BY ct.displayOrder ASC, ct.typeName ASC")
    List<java.util.Map<String, Object>> findContractTypeOptionsByCategory(@Param("category") ContractType.ContractCategory category);

    /**
     * Check if contract type is safe to delete (no active contracts)
     */
    @Query("SELECT CASE WHEN COUNT(c) = 0 THEN true ELSE false END " +
            "FROM Contract c WHERE c.contractType.id = :contractTypeId AND c.isActive = true")
    boolean canSafelyDelete(@Param("contractTypeId") Long contractTypeId);
}