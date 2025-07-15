package com.bp.contractrepository.contract.repository;

import com.bp.contractrepository.contract.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contract Repository - Database access layer for Contract entities
 * Provides CRUD operations and custom queries for contract management
 *
 * @author Contract Management Team
 */
@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

    // =====================================
    // Basic Finder Methods
    // =====================================

    /**
     * Find contract by unique contract number
     */
    Optional<Contract> findByContractNumberAndIsActiveTrue(String contractNumber);

    /**
     * Find contract by contract number (including inactive)
     */
    Optional<Contract> findByContractNumber(String contractNumber);

    /**
     * Check if contract number already exists
     */
    boolean existsByContractNumber(String contractNumber);

    /**
     * Find all active contracts
     */
    @Query("SELECT c FROM Contract c WHERE c.isActive = true ORDER BY c.createdDate DESC")
    Page<Contract> findAllActive(Pageable pageable);

    /**
     * Find all active contracts (list)
     */
    List<Contract> findByIsActiveTrueOrderByCreatedDateDesc();

    // =====================================
    // Customer-based Queries
    // =====================================

    /**
     * Find contracts by customer ID
     */
    Page<Contract> findByCustomerIdAndIsActiveTrueOrderByCreatedDateDesc(String customerId, Pageable pageable);

    /**
     * Find contracts by customer name (case-insensitive search)
     */
    @Query("SELECT c FROM Contract c WHERE LOWER(c.customerName) LIKE LOWER(CONCAT('%', :customerName, '%')) AND c.isActive = true ORDER BY c.createdDate DESC")
    Page<Contract> findByCustomerNameContainingIgnoreCaseAndIsActiveTrue(@Param("customerName") String customerName, Pageable pageable);

    /**
     * Count active contracts by customer
     */
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.customerId = :customerId AND c.isActive = true")
    long countActiveContractsByCustomer(@Param("customerId") String customerId);

    // =====================================
    // Status-based Queries
    // =====================================

    /**
     * Find contracts by status
     */
    Page<Contract> findByStatusAndIsActiveTrueOrderByCreatedDateDesc(Contract.ContractStatus status, Pageable pageable);

    /**
     * Find active contracts (status = ACTIVE)
     */
    @Query("SELECT c FROM Contract c WHERE c.status = 'ACTIVE' AND c.isActive = true AND c.startDate <= CURRENT_DATE AND c.endDate >= CURRENT_DATE ORDER BY c.endDate ASC")
    List<Contract> findCurrentlyActiveContracts();

    /**
     * Find expired contracts
     */
    @Query("SELECT c FROM Contract c WHERE c.endDate < CURRENT_DATE AND c.isActive = true ORDER BY c.endDate DESC")
    List<Contract> findExpiredContracts();

    /**
     * Find contracts expiring soon
     */
    @Query("SELECT c FROM Contract c WHERE c.endDate BETWEEN CURRENT_DATE AND :thresholdDate AND c.status = 'ACTIVE' AND c.isActive = true ORDER BY c.endDate ASC")
    List<Contract> findContractsExpiringSoon(@Param("thresholdDate") LocalDate thresholdDate);

    /**
     * Find contracts expiring in specified days
     */
    @Query("SELECT c FROM Contract c WHERE c.endDate BETWEEN CURRENT_DATE AND (CURRENT_DATE + :days) AND c.status = 'ACTIVE' AND c.isActive = true ORDER BY c.endDate ASC")
    List<Contract> findContractsExpiringInDays(@Param("days") int days);

    // =====================================
    // Contract Type Queries
    // =====================================

    /**
     * Find contracts by contract type
     */
    @Query("SELECT c FROM Contract c WHERE c.contractType.id = :contractTypeId AND c.isActive = true ORDER BY c.createdDate DESC")
    Page<Contract> findByContractTypeId(@Param("contractTypeId") Long contractTypeId, Pageable pageable);

    /**
     * Find contracts by contract type code
     */
    @Query("SELECT c FROM Contract c WHERE c.contractType.typeCode = :typeCode AND c.isActive = true ORDER BY c.createdDate DESC")
    List<Contract> findByContractTypeCode(@Param("typeCode") String typeCode);

    /**
     * Count contracts by contract type
     */
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.contractType.id = :contractTypeId AND c.isActive = true")
    long countByContractTypeId(@Param("contractTypeId") Long contractTypeId);

    // =====================================
    // Date Range Queries
    // =====================================

    /**
     * Find contracts by date range
     */
    @Query("SELECT c FROM Contract c WHERE c.startDate >= :startDate AND c.endDate <= :endDate AND c.isActive = true ORDER BY c.startDate ASC")
    List<Contract> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find contracts created between dates
     */
    @Query("SELECT c FROM Contract c WHERE DATE(c.createdDate) BETWEEN :fromDate AND :toDate AND c.isActive = true ORDER BY c.createdDate DESC")
    List<Contract> findByCreatedDateBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    /**
     * Find contracts with renewals due
     */
    @Query("SELECT c FROM Contract c WHERE c.renewalDate IS NOT NULL AND c.renewalDate <= :date AND c.status = 'ACTIVE' AND c.isActive = true ORDER BY c.renewalDate ASC")
    List<Contract> findContractsWithRenewalsDue(@Param("date") LocalDate date);

    // =====================================
    // Financial Queries
    // =====================================

    /**
     * Find high-value contracts (above threshold)
     */
    @Query("SELECT c FROM Contract c WHERE c.contractValue >= :threshold AND c.isActive = true ORDER BY c.contractValue DESC")
    List<Contract> findHighValueContracts(@Param("threshold") BigDecimal threshold);

    /**
     * Calculate total contract value
     */
    @Query("SELECT COALESCE(SUM(c.contractValue), 0) FROM Contract c WHERE c.isActive = true")
    BigDecimal calculateTotalContractValue();

    /**
     * Calculate total contract value by status
     */
    @Query("SELECT COALESCE(SUM(c.contractValue), 0) FROM Contract c WHERE c.status = :status AND c.isActive = true")
    BigDecimal calculateTotalValueByStatus(@Param("status") Contract.ContractStatus status);

    /**
     * Find contracts by value range
     */
    @Query("SELECT c FROM Contract c WHERE c.contractValue BETWEEN :minValue AND :maxValue AND c.isActive = true ORDER BY c.contractValue DESC")
    List<Contract> findByValueRange(@Param("minValue") BigDecimal minValue, @Param("maxValue") BigDecimal maxValue);

    // =====================================
    // Department and Business Queries
    // =====================================

    /**
     * Find contracts by internal department
     */
    Page<Contract> findByInternalDepartmentAndIsActiveTrueOrderByCreatedDateDesc(String internalDepartment, Pageable pageable);

    /**
     * Find contracts by business owner
     */
    @Query("SELECT c FROM Contract c WHERE LOWER(c.businessOwner) LIKE LOWER(CONCAT('%', :businessOwner, '%')) AND c.isActive = true ORDER BY c.createdDate DESC")
    List<Contract> findByBusinessOwnerContainingIgnoreCase(@Param("businessOwner") String businessOwner);

    /**
     * Find contracts by relationship manager
     */
    @Query("SELECT c FROM Contract c WHERE LOWER(c.relationshipManager) LIKE LOWER(CONCAT('%', :relationshipManager, '%')) AND c.isActive = true ORDER BY c.createdDate DESC")
    List<Contract> findByRelationshipManagerContainingIgnoreCase(@Param("relationshipManager") String relationshipManager);

    // =====================================
    // Search and Filter Queries
    // =====================================

    /**
     * Global search across multiple fields
     */
    @Query("SELECT c FROM Contract c WHERE c.isActive = true AND (" +
            "LOWER(c.contractNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.customerName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.externalParty) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
            ") ORDER BY c.createdDate DESC")
    Page<Contract> searchContracts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT c FROM Contract c WHERE c.isActive = true " +
            "AND (:contractTypeId IS NULL OR c.contractType.id = :contractTypeId) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:customerId IS NULL OR c.customerId = :customerId) " +
            "AND (:department IS NULL OR c.internalDepartment = :department) " +
            "AND (:startDate IS NULL OR c.startDate >= :startDate) " +
            "AND (:endDate IS NULL OR c.endDate <= :endDate) " +
            "ORDER BY c.createdDate DESC")
    Page<Contract> findWithCriteria(
            @Param("contractTypeId") Long contractTypeId,
            @Param("status") Contract.ContractStatus status,
            @Param("customerId") String customerId,
            @Param("department") String department,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // =====================================
    // Integration Queries (T24/Evolan)
    // =====================================

    /**
     * Find contracts by T24 customer ID
     */
    List<Contract> findByT24CustomerIdAndIsActiveTrueOrderByCreatedDateDesc(String t24CustomerId);

    /**
     * Find contracts by source system
     */
    @Query("SELECT c FROM Contract c WHERE c.sourceSystem = :sourceSystem AND c.isActive = true ORDER BY c.createdDate DESC")
    List<Contract> findBySourceSystem(@Param("sourceSystem") String sourceSystem);

    /**
     * Find contracts needing customer sync (old or missing T24 data)
     */
    @Query("SELECT c FROM Contract c WHERE c.isActive = true AND (" +
            "c.t24CustomerId IS NULL OR " +
            "c.lastModifiedDate < :syncThreshold" +
            ") ORDER BY c.lastModifiedDate ASC")
    List<Contract> findContractsNeedingCustomerSync(@Param("syncThreshold") LocalDate syncThreshold);

    // =====================================
    // Reporting Queries
    // =====================================

    /**
     * Contract summary statistics
     */
    @Query("SELECT " +
            "COUNT(c) as totalContracts, " +
            "COUNT(CASE WHEN c.status = 'ACTIVE' THEN 1 END) as activeContracts, " +
            "COUNT(CASE WHEN c.endDate < CURRENT_DATE THEN 1 END) as expiredContracts, " +
            "COUNT(CASE WHEN c.endDate BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 30 DAY) THEN 1 END) as expiringSoon, " +
            "COALESCE(SUM(c.contractValue), 0) as totalValue " +
            "FROM Contract c WHERE c.isActive = true")
    Object[] getContractStatistics();

    /**
     * Contracts by status count
     */
    @Query("SELECT c.status, COUNT(c) FROM Contract c WHERE c.isActive = true GROUP BY c.status")
    List<Object[]> getContractCountsByStatus();

    /**
     * Contracts by department count
     */
    @Query("SELECT c.internalDepartment, COUNT(c) FROM Contract c WHERE c.isActive = true GROUP BY c.internalDepartment ORDER BY COUNT(c) DESC")
    List<Object[]> getContractCountsByDepartment();

    /**
     * Monthly contract creation trends
     */
    @Query("SELECT YEAR(c.createdDate)  , MONTH(c.createdDate), COUNT(c) " +
            "FROM Contract c WHERE c.isActive = true " +
            "AND c.createdDate >= :fromDate " +
            "GROUP BY YEAR(c.createdDate), MONTH(c.createdDate) " +
            "ORDER BY YEAR(c.createdDate), MONTH(c.createdDate)")
    List<Object[]> getMonthlyContractCreationTrends(@Param("fromDate") LocalDate fromDate);
}