package com.bp.contractrepository.contract.service;

import com.bp.contractrepository.contract.dto.ContractDTO;

import com.bp.contractrepository.contract.dto.CreateContractRequest;
import com.bp.contractrepository.contract.dto.UpdateContractRequest;
import com.bp.contractrepository.contract.entity.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Contract Service Interface
 * Defines business operations for contract management
 *
 * @author Contract Management Team
 */
public interface ContractService {

    // =====================================
    // CRUD Operations
    // =====================================

    /**
     * Create a new contract
     */
    ContractDTO createContract(CreateContractRequest request);

    /**
     * Update an existing contract
     */
    ContractDTO updateContract(Long id, UpdateContractRequest request);

    /**
     * Get contract by ID
     */
    ContractDTO getContractById(Long id);

    /**
     * Get contract entity by ID (for internal use)
     */
    Contract findById(Long id);

    /**
     * Get contract by contract number
     */
    ContractDTO getContractByNumber(String contractNumber);

    /**
     * Get all active contracts with pagination
     */
    Page<ContractDTO> getAllActiveContracts(Pageable pageable);

    /**
     * Soft delete a contract
     */
    void deleteContract(Long id);

    /**
     * Restore a soft-deleted contract
     */
    ContractDTO restoreContract(Long id);

    // =====================================
    // Search and Filter Operations
    // =====================================

    /**
     * Search contracts by text across multiple fields
     */
    Page<ContractDTO> searchContracts(String searchTerm, Pageable pageable);

    /**
     * Find contracts with advanced criteria
     */
    Page<ContractDTO> findContractsWithCriteria(
            Long contractTypeId,
            Contract.ContractStatus status,
            String customerId,
            String department,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    /**
     * Find contracts by customer
     */
    Page<ContractDTO> getContractsByCustomer(String customerId, Pageable pageable);

    /**
     * Find contracts by department
     */
    Page<ContractDTO> getContractsByDepartment(String department, Pageable pageable);

    /**
     * Find contracts by contract type
     */
    Page<ContractDTO> getContractsByType(Long contractTypeId, Pageable pageable);

    // =====================================
    // Lifecycle Management
    // =====================================

    /**
     * Get contracts expiring soon (within specified days)
     */
    List<ContractDTO> getContractsExpiringSoon(int days);

    /**
     * Get expired contracts
     */
    List<ContractDTO> getExpiredContracts();

    /**
     * Get contracts needing renewal
     */
    List<ContractDTO> getContractsNeedingRenewal();

    /**
     * Get currently active contracts
     */
    List<ContractDTO> getCurrentlyActiveContracts();

    /**
     * Renew a contract (create new contract based on existing)
     */
    ContractDTO renewContract(Long contractId, LocalDate newStartDate, LocalDate newEndDate);

    /**
     * Extend contract end date
     */
    ContractDTO extendContract(Long contractId, LocalDate newEndDate);

    /**
     * Activate a draft contract
     */
    ContractDTO activateContract(Long contractId);

    /**
     * Suspend an active contract
     */
    ContractDTO suspendContract(Long contractId, String reason);

    /**
     * Terminate a contract
     */
    ContractDTO terminateContract(Long contractId, String reason);

    // =====================================
    // Financial Operations
    // =====================================

    /**
     * Get high-value contracts (above threshold)
     */
    List<ContractDTO> getHighValueContracts(BigDecimal threshold);

    /**
     * Calculate total contract value
     */
    BigDecimal getTotalContractValue();

    /**
     * Calculate total value by status
     */
    BigDecimal getTotalValueByStatus(Contract.ContractStatus status);

    /**
     * Get contracts by value range
     */
    List<ContractDTO> getContractsByValueRange(BigDecimal minValue, BigDecimal maxValue);

    /**
     * Update contract value
     */
    ContractDTO updateContractValue(Long contractId, BigDecimal newValue);

    // =====================================
    // Integration Operations
    // =====================================

    /**
     * Sync contract customer data from T24/Evolan
     */
    ContractDTO syncCustomerData(Long contractId);

    /**
     * Bulk sync customer data for all contracts
     */
    void bulkSyncCustomerData();

    /**
     * Find contracts by T24 customer ID
     */
    List<ContractDTO> getContractsByT24CustomerId(String t24CustomerId);

    /**
     * Find contracts by source system
     */
    List<ContractDTO> getContractsBySourceSystem(String sourceSystem);

    // =====================================
    // Validation Operations
    // =====================================

    /**
     * Validate contract number uniqueness
     */
    boolean isContractNumberUnique(String contractNumber);

    /**
     * Validate contract dates
     */
    void validateContractDates(LocalDate startDate, LocalDate endDate);

    /**
     * Validate customer exists in core banking
     */
    void validateCustomerExists(String customerId);

    /**
     * Validate contract can be updated
     */
    void validateContractCanBeUpdated(Long contractId);

    /**
     * Validate contract can be deleted
     */
    void validateContractCanBeDeleted(Long contractId);

    // =====================================
    // Reporting Operations
    // =====================================

    /**
     * Get contract summary statistics
     */
    ContractStatistics getContractStatistics();

    /**
     * Get contract counts by status
     */
    List<StatusCount> getContractCountsByStatus();

    /**
     * Get contract counts by department
     */
    List<DepartmentCount> getContractCountsByDepartment();

    /**
     * Get monthly contract creation trends
     */
    List<MonthlyTrend> getMonthlyCreationTrends(LocalDate fromDate);

    /**
     * Generate contract expiration report
     */
    List<ContractDTO> generateExpirationReport(int days);

    // =====================================
    // Utility Operations
    // =====================================

    /**
     * Generate next contract number
     */
    String generateContractNumber(String contractTypeCode);

    /**
     * Check if user can access contract
     */
    boolean canUserAccessContract(Long contractId, String userId);

    /**
     * Get contract history
     */
    List<ContractHistoryDTO> getContractHistory(Long contractId);

    // =====================================
    // Inner Classes for Reporting
    // =====================================

    /**
     * Contract statistics summary
     */
    record ContractStatistics(
            long totalContracts,
            long activeContracts,
            long expiredContracts,
            long expiringSoon,
            BigDecimal totalValue,
            BigDecimal activeValue
    ) {}

    /**
     * Status count summary
     */
    record StatusCount(
            Contract.ContractStatus status,
            long count
    ) {}

    /**
     * Department count summary
     */
    record DepartmentCount(
            String department,
            long count
    ) {}

    /**
     * Monthly trend data
     */
    record MonthlyTrend(
            int year,
            int month,
            long count
    ) {}

    /**
     * Contract history entry
     */
    record ContractHistoryDTO(
            String action,
            String fieldName,
            String oldValue,
            String newValue,
            String performedBy,
            LocalDate actionDate,
            String description
    ) {}
}