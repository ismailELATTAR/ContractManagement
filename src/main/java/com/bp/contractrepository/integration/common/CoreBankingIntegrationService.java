package com.bp.contractrepository.integration.common;

import com.bp.contractrepository.customer.dto.CustomerDTO;

import java.util.List;

/**
 * Core Banking Integration Service Interface
 * Provides unified interface for T24, Evolan, and other core banking systems
 *
 * @author Contract Management Team
 */
public interface CoreBankingIntegrationService {

    // =====================================
    // Customer Operations
    // =====================================

    /**
     * Get customer by ID from core banking system
     *
     * @param customerId Customer identifier
     * @return Customer information
     * @throws com.bp.contractrepository.shared.exception.BusinessException if customer not found
     */
    CustomerDTO getCustomerById(String customerId);

    /**
     * Search customers by name
     *
     * @param customerName Customer name (partial match)
     * @return List of matching customers
     */
    List<CustomerDTO> searchCustomersByName(String customerName);

    /**
     * Validate if customer exists and is active
     *
     * @param customerId Customer identifier
     * @return true if customer exists and is active
     */
    boolean isCustomerValid(String customerId);

    /**
     * Get customer accounts information
     *
     * @param customerId Customer identifier
     * @return List of customer accounts
     */
    List<AccountSummaryDTO> getCustomerAccounts(String customerId);

    /**
     * Refresh customer data from core banking
     *
     * @param customerId Customer identifier
     * @return Updated customer information
     */
    CustomerDTO refreshCustomerData(String customerId);

    // =====================================
    // System Information
    // =====================================

    /**
     * Get the name of the core banking system being used
     *
     * @return System name (T24, EVOLAN, MOCK)
     */
    String getSystemName();

    /**
     * Check if the core banking system is available
     *
     * @return true if system is available
     */
    boolean isSystemAvailable();

    /**
     * Get system health status
     *
     * @return Health status information
     */
    SystemHealthDTO getSystemHealth();

    // =====================================
    // Batch Operations
    // =====================================

    /**
     * Bulk refresh customer data
     *
     * @param customerIds List of customer identifiers
     * @return List of updated customer information
     */
    List<CustomerDTO> bulkRefreshCustomerData(List<String> customerIds);

    /**
     * Get customers that need data refresh
     *
     * @param daysSinceLastSync Number of days since last sync
     * @return List of customer IDs needing refresh
     */
    List<String> getCustomersNeedingRefresh(int daysSinceLastSync);

    // =====================================
    // Supporting DTOs
    // =====================================

    /**
     * Account Summary DTO
     */
    record AccountSummaryDTO(
            String accountId,
            String accountNumber,
            String accountType,
            String currency,
            java.math.BigDecimal balance,
            String status
    ) {}

    /**
     * System Health DTO
     */
    record SystemHealthDTO(
            String systemName,
            boolean isAvailable,
            String status,
            long responseTimeMs,
            String lastCheckTime,
            String version
    ) {}
}