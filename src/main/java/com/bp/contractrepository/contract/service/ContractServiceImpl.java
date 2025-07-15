package com.bp.contractrepository.contract.service;

import com.bp.contractrepository.contract.dto.ContractDTO;
import com.bp.contractrepository.contract.dto.CreateContractRequest;
import com.bp.contractrepository.contract.dto.UpdateContractRequest;
import com.bp.contractrepository.contract.entity.Contract;
import com.bp.contractrepository.contract.entity.ContractType;
import com.bp.contractrepository.contract.repository.ContractRepository;
import com.bp.contractrepository.contract.repository.ContractTypeRepository;
import com.bp.contractrepository.shared.exception.BusinessException;
import com.bp.contractrepository.shared.exception.ResourceNotFoundException;
import com.bp.contractrepository.shared.mapper.ContractMapper;
import com.bp.contractrepository.shared.security.SecurityUtils;
import com.bp.contractrepository.integration.common.CoreBankingIntegrationService;
import com.bp.contractrepository.customer.dto.CustomerDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contract Service Implementation
 * Implements all business logic for contract management
 *
 * @author Contract Management Team
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final ContractMapper contractMapper;
    private final CoreBankingIntegrationService coreBankingService;
    private final SecurityUtils securityUtils;

    // =====================================
    // CRUD Operations
    // =====================================

    @Override
    public ContractDTO createContract(CreateContractRequest request) {
        log.info("Creating new contract with title: {}", request.getTitle());

        // Validate request
        validateCreateContractRequest(request);

        // Validate customer exists in core banking
        CustomerDTO customer = validateAndGetCustomer(request.getCustomerId());

        // Get contract type
        ContractType contractType = getContractType(request.getContractTypeId());

        // Generate contract number if not provided
        String contractNumber = StringUtils.hasText(request.getContractNumber())
                ? request.getContractNumber()
                : generateContractNumber(contractType.getTypeCode());

        // Build contract entity
        Contract contract = Contract.builder()
                .contractNumber(contractNumber)
                .title(request.getTitle())
                .description(request.getDescription())
                .contractType(contractType)
                .status(Contract.ContractStatus.DRAFT)
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerType(customer.getCustomerType())
                .t24CustomerId(customer.getT24CustomerId())
                .internalDepartment(request.getInternalDepartment())
                .externalParty(request.getExternalParty())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .renewalDate(request.getRenewalDate())
                .contractValue(request.getContractValue())
                .currency(request.getCurrency() != null ? request.getCurrency() : "MAD")
                .paymentTerms(request.getPaymentTerms())
                .riskLevel(request.getRiskLevel())
                .businessOwner(request.getBusinessOwner())
                .primaryContact(request.getPrimaryContact())
                .relationshipManager(customer.getRelationshipManager())
                .autoRenewal(request.getAutoRenewal() != null ? request.getAutoRenewal() : false)
                .reminderDays(request.getReminderDays() != null ? request.getReminderDays() : contractType.getEffectiveReminderDays())
                .internalNotes(request.getInternalNotes())
                .complianceNotes(request.getComplianceNotes())
                .sourceSystem(determineSourceSystem(customer))
                .build();

        // Save contract
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract created successfully with ID: {} and number: {}",
                savedContract.getId(), savedContract.getContractNumber());

        return contractMapper.toDTO(savedContract);
    }

    @Override
    public ContractDTO updateContract(Long id, UpdateContractRequest request) {
        log.info("Updating contract with ID: {}", id);

        Contract contract = findEntityById(id);
        validateContractCanBeUpdated(id);

        // Update fields
        if (StringUtils.hasText(request.getTitle())) {
            contract.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getDescription())) {
            contract.setDescription(request.getDescription());
        }
        if (request.getContractTypeId() != null) {
            ContractType contractType = getContractType(request.getContractTypeId());
            contract.setContractType(contractType);
        }
        if (request.getStartDate() != null) {
            contract.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            contract.setEndDate(request.getEndDate());
        }
        if (request.getRenewalDate() != null) {
            contract.setRenewalDate(request.getRenewalDate());
        }
        if (request.getContractValue() != null) {
            contract.setContractValue(request.getContractValue());
        }
        if (StringUtils.hasText(request.getCurrency())) {
            contract.setCurrency(request.getCurrency());
        }
        if (StringUtils.hasText(request.getPaymentTerms())) {
            contract.setPaymentTerms(request.getPaymentTerms());
        }
        if (request.getRiskLevel() != null) {
            contract.setRiskLevel(request.getRiskLevel());
        }
        if (StringUtils.hasText(request.getBusinessOwner())) {
            contract.setBusinessOwner(request.getBusinessOwner());
        }
        if (StringUtils.hasText(request.getPrimaryContact())) {
            contract.setPrimaryContact(request.getPrimaryContact());
        }
        if (request.getAutoRenewal() != null) {
            contract.setAutoRenewal(request.getAutoRenewal());
        }
        if (request.getReminderDays() != null) {
            contract.setReminderDays(request.getReminderDays());
        }
        if (StringUtils.hasText(request.getInternalNotes())) {
            contract.setInternalNotes(request.getInternalNotes());
        }
        if (StringUtils.hasText(request.getComplianceNotes())) {
            contract.setComplianceNotes(request.getComplianceNotes());
        }

        // Validate dates if both are present
        if (contract.getStartDate() != null && contract.getEndDate() != null) {
            validateContractDates(contract.getStartDate(), contract.getEndDate());
        }

        Contract savedContract = contractRepository.save(contract);

        log.info("Contract updated successfully with ID: {}", savedContract.getId());

        return contractMapper.toDTO(savedContract);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractDTO getContractById(Long id) {
        Contract contract = findEntityById(id);
        return contractMapper.toDTO(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public Contract findById(Long id) {
        return findEntityById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ContractDTO getContractByNumber(String contractNumber) {
        Contract contract = contractRepository.findByContractNumberAndIsActiveTrue(contractNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with number: " + contractNumber));
        return contractMapper.toDTO(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDTO> getAllActiveContracts(Pageable pageable) {
        Page<Contract> contracts = contractRepository.findAllActive(pageable);
        return contracts.map(contractMapper::toDTO);
    }

    @Override
    public void deleteContract(Long id) {
        log.info("Soft deleting contract with ID: {}", id);

        Contract contract = findEntityById(id);
        validateContractCanBeDeleted(id);

        contract.softDelete();
        contractRepository.save(contract);

        log.info("Contract soft deleted successfully with ID: {}", id);
    }

    @Override
    public ContractDTO restoreContract(Long id) {
        log.info("Restoring contract with ID: {}", id);

        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + id));

        contract.restore();
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract restored successfully with ID: {}", id);

        return contractMapper.toDTO(savedContract);
    }

    // =====================================
    // Search and Filter Operations
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDTO> searchContracts(String searchTerm, Pageable pageable) {
        Page<Contract> contracts = contractRepository.searchContracts(searchTerm, pageable);
        return contracts.map(contractMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDTO> findContractsWithCriteria(Long contractTypeId, Contract.ContractStatus status,
                                                       String customerId, String department, LocalDate startDate,
                                                       LocalDate endDate, Pageable pageable) {
        Page<Contract> contracts = contractRepository.findWithCriteria(
                contractTypeId, status, customerId, department, startDate, endDate, pageable);
        return contracts.map(contractMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDTO> getContractsByCustomer(String customerId, Pageable pageable) {
        Page<Contract> contracts = contractRepository.findByCustomerIdAndIsActiveTrueOrderByCreatedDateDesc(customerId, pageable);
        return contracts.map(contractMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDTO> getContractsByDepartment(String department, Pageable pageable) {
        Page<Contract> contracts = contractRepository.findByInternalDepartmentAndIsActiveTrueOrderByCreatedDateDesc(department, pageable);
        return contracts.map(contractMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContractDTO> getContractsByType(Long contractTypeId, Pageable pageable) {
        Page<Contract> contracts = contractRepository.findByContractTypeId(contractTypeId, pageable);
        return contracts.map(contractMapper::toDTO);
    }

    // =====================================
    // Lifecycle Management
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getContractsExpiringSoon(int days) {
        List<Contract> contracts = contractRepository.findContractsExpiringInDays(days);
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getExpiredContracts() {
        List<Contract> contracts = contractRepository.findExpiredContracts();
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getContractsNeedingRenewal() {
        LocalDate thresholdDate = LocalDate.now().plusDays(90);
        List<Contract> contracts = contractRepository.findContractsWithRenewalsDue(thresholdDate);
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getCurrentlyActiveContracts() {
        List<Contract> contracts = contractRepository.findCurrentlyActiveContracts();
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDTO renewContract(Long contractId, LocalDate newStartDate, LocalDate newEndDate) {
        log.info("Renewing contract with ID: {}", contractId);

        Contract originalContract = findEntityById(contractId);

        // Validate original contract can be renewed
        if (originalContract.getStatus() != Contract.ContractStatus.ACTIVE) {
            throw new BusinessException("Only active contracts can be renewed");
        }

        validateContractDates(newStartDate, newEndDate);

        // Create renewed contract based on original
        Contract renewedContract = Contract.builder()
                .contractNumber(generateContractNumber(originalContract.getContractType().getTypeCode()))
                .title(originalContract.getTitle() + " (Renewed)")
                .description(originalContract.getDescription())
                .contractType(originalContract.getContractType())
                .status(Contract.ContractStatus.DRAFT)
                .customerId(originalContract.getCustomerId())
                .customerName(originalContract.getCustomerName())
                .customerType(originalContract.getCustomerType())
                .t24CustomerId(originalContract.getT24CustomerId())
                .internalDepartment(originalContract.getInternalDepartment())
                .externalParty(originalContract.getExternalParty())
                .startDate(newStartDate)
                .endDate(newEndDate)
                .contractValue(originalContract.getContractValue())
                .currency(originalContract.getCurrency())
                .paymentTerms(originalContract.getPaymentTerms())
                .riskLevel(originalContract.getRiskLevel())
                .businessOwner(originalContract.getBusinessOwner())
                .primaryContact(originalContract.getPrimaryContact())
                .relationshipManager(originalContract.getRelationshipManager())
                .autoRenewal(originalContract.getAutoRenewal())
                .reminderDays(originalContract.getReminderDays())
                .sourceSystem(originalContract.getSourceSystem())
                .build();

        // Update original contract status
        originalContract.setStatus(Contract.ContractStatus.RENEWED);
        contractRepository.save(originalContract);

        // Save renewed contract
        Contract savedContract = contractRepository.save(renewedContract);

        log.info("Contract renewed successfully. Original ID: {}, New ID: {}", contractId, savedContract.getId());

        return contractMapper.toDTO(savedContract);
    }

    @Override
    public ContractDTO extendContract(Long contractId, LocalDate newEndDate) {
        log.info("Extending contract with ID: {} to new end date: {}", contractId, newEndDate);

        Contract contract = findEntityById(contractId);

        if (contract.getEndDate() != null && newEndDate.isBefore(contract.getEndDate())) {
            throw new BusinessException("New end date cannot be before current end date");
        }

        contract.setEndDate(newEndDate);
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract extended successfully with ID: {}", contractId);

        return contractMapper.toDTO(savedContract);
    }

    @Override
    public ContractDTO activateContract(Long contractId) {
        log.info("Activating contract with ID: {}", contractId);

        Contract contract = findEntityById(contractId);

        if (contract.getStatus() != Contract.ContractStatus.DRAFT) {
            throw new BusinessException("Only draft contracts can be activated");
        }

        // Validate contract is complete
        validateContractForActivation(contract);

        contract.setStatus(Contract.ContractStatus.ACTIVE);
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract activated successfully with ID: {}", contractId);

        return contractMapper.toDTO(savedContract);
    }

    @Override
    public ContractDTO suspendContract(Long contractId, String reason) {
        log.info("Suspending contract with ID: {} for reason: {}", contractId, reason);

        Contract contract = findEntityById(contractId);

        if (contract.getStatus() != Contract.ContractStatus.ACTIVE) {
            throw new BusinessException("Only active contracts can be suspended");
        }

        contract.setStatus(Contract.ContractStatus.SUSPENDED);
        contract.setComplianceNotes(appendNote(contract.getComplianceNotes(), "Suspended: " + reason));
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract suspended successfully with ID: {}", contractId);

        return contractMapper.toDTO(savedContract);
    }

    @Override
    public ContractDTO terminateContract(Long contractId, String reason) {
        log.info("Terminating contract with ID: {} for reason: {}", contractId, reason);

        Contract contract = findEntityById(contractId);

        if (contract.getStatus() != Contract.ContractStatus.ACTIVE &&
                contract.getStatus() != Contract.ContractStatus.SUSPENDED) {
            throw new BusinessException("Only active or suspended contracts can be terminated");
        }

        contract.setStatus(Contract.ContractStatus.TERMINATED);
        contract.setComplianceNotes(appendNote(contract.getComplianceNotes(), "Terminated: " + reason));
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract terminated successfully with ID: {}", contractId);

        return contractMapper.toDTO(savedContract);
    }

    // =====================================
    // Financial Operations
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getHighValueContracts(BigDecimal threshold) {
        List<Contract> contracts = contractRepository.findHighValueContracts(threshold);
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalContractValue() {
        return contractRepository.calculateTotalContractValue();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalValueByStatus(Contract.ContractStatus status) {
        return contractRepository.calculateTotalValueByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getContractsByValueRange(BigDecimal minValue, BigDecimal maxValue) {
        List<Contract> contracts = contractRepository.findByValueRange(minValue, maxValue);
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContractDTO updateContractValue(Long contractId, BigDecimal newValue) {
        log.info("Updating contract value for ID: {} to: {}", contractId, newValue);

        Contract contract = findEntityById(contractId);

        if (newValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Contract value must be positive");
        }

        contract.setContractValue(newValue);
        Contract savedContract = contractRepository.save(contract);

        log.info("Contract value updated successfully for ID: {}", contractId);

        return contractMapper.toDTO(savedContract);
    }

    // =====================================
    // Integration Operations
    // =====================================

    @Override
    public ContractDTO syncCustomerData(Long contractId) {
        log.info("Syncing customer data for contract ID: {}", contractId);

        Contract contract = findEntityById(contractId);
        CustomerDTO customer = validateAndGetCustomer(contract.getCustomerId());

        // Update customer fields from core banking
        contract.setCustomerName(customer.getCustomerName());
        contract.setCustomerType(customer.getCustomerType());
        contract.setT24CustomerId(customer.getT24CustomerId());
        contract.setRelationshipManager(customer.getRelationshipManager());

        Contract savedContract = contractRepository.save(contract);

        log.info("Customer data synced successfully for contract ID: {}", contractId);

        return contractMapper.toDTO(savedContract);
    }

    @Override
    public void bulkSyncCustomerData() {
        log.info("Starting bulk customer data sync");

        LocalDate syncThreshold = LocalDate.now().minusDays(30);
        List<Contract> contractsToSync = contractRepository.findContractsNeedingCustomerSync(syncThreshold);

        int successCount = 0;
        int errorCount = 0;

        for (Contract contract : contractsToSync) {
            try {
                syncCustomerData(contract.getId());
                successCount++;
            } catch (Exception e) {
                log.error("Failed to sync customer data for contract ID: {}", contract.getId(), e);
                errorCount++;
            }
        }

        log.info("Bulk customer sync completed. Success: {}, Errors: {}", successCount, errorCount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getContractsByT24CustomerId(String t24CustomerId) {
        List<Contract> contracts = contractRepository.findByT24CustomerIdAndIsActiveTrueOrderByCreatedDateDesc(t24CustomerId);
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> getContractsBySourceSystem(String sourceSystem) {
        List<Contract> contracts = contractRepository.findBySourceSystem(sourceSystem);
        return contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
    }

    // =====================================
    // Validation Operations
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public boolean isContractNumberUnique(String contractNumber) {
        return !contractRepository.existsByContractNumber(contractNumber);
    }

    @Override
    public void validateContractDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new BusinessException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new BusinessException("Start date cannot be after end date");
        }

        if (endDate.isBefore(LocalDate.now().minusDays(1))) {
            throw new BusinessException("End date cannot be in the past");
        }
    }

    @Override
    public void validateCustomerExists(String customerId) {
        validateAndGetCustomer(customerId);
    }

    @Override
    public void validateContractCanBeUpdated(Long contractId) {
        Contract contract = findEntityById(contractId);

        if (contract.getStatus() == Contract.ContractStatus.TERMINATED) {
            throw new BusinessException("Terminated contracts cannot be updated");
        }

        if (contract.getStatus() == Contract.ContractStatus.EXPIRED) {
            throw new BusinessException("Expired contracts cannot be updated");
        }
    }

    @Override
    public void validateContractCanBeDeleted(Long contractId) {
        Contract contract = findEntityById(contractId);

        if (contract.getStatus() == Contract.ContractStatus.ACTIVE) {
            throw new BusinessException("Active contracts cannot be deleted. Please terminate first.");
        }
    }

    // =====================================
    // Reporting Operations
    // =====================================

    @Override
    @Transactional(readOnly = true)
    public ContractStatistics getContractStatistics() {
        Object[] stats = contractRepository.getContractStatistics();

        if (stats.length > 0 && stats[0] != null) {
            Object[] row = (Object[]) stats[0];
            return new ContractStatistics(
                    ((Number) row[0]).longValue(),    // totalContracts
                    ((Number) row[1]).longValue(),    // activeContracts
                    ((Number) row[2]).longValue(),    // expiredContracts
                    ((Number) row[3]).longValue(),    // expiringSoon
                    (BigDecimal) row[4],              // totalValue
                    getTotalValueByStatus(Contract.ContractStatus.ACTIVE) // activeValue
            );
        }

        return new ContractStatistics(0, 0, 0, 0, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatusCount> getContractCountsByStatus() {
        List<Object[]> results = contractRepository.getContractCountsByStatus();
        return results.stream()
                .map(row -> new StatusCount((Contract.ContractStatus) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentCount> getContractCountsByDepartment() {
        List<Object[]> results = contractRepository.getContractCountsByDepartment();
        return results.stream()
                .map(row -> new DepartmentCount((String) row[0], ((Number) row[1]).longValue()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyTrend> getMonthlyCreationTrends(LocalDate fromDate) {
        List<Object[]> results = contractRepository.getMonthlyContractCreationTrends(fromDate);
        return results.stream()
                .map(row -> new MonthlyTrend(
                        ((Number) row[0]).intValue(),  // year
                        ((Number) row[1]).intValue(),  // month
                        ((Number) row[2]).longValue()  // count
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractDTO> generateExpirationReport(int days) {
        return getContractsExpiringSoon(days);
    }

    // =====================================
    // Utility Operations
    // =====================================

    @Override
    public String generateContractNumber(String contractTypeCode) {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = "BP-" + year + "-" + contractTypeCode + "-";

        // Find the next sequential number
        long count = contractRepository.count() + 1;
        String sequence = String.format("%04d", count);

        String contractNumber = prefix + sequence;

        // Ensure uniqueness
        while (contractRepository.existsByContractNumber(contractNumber)) {
            count++;
            sequence = String.format("%04d", count);
            contractNumber = prefix + sequence;
        }

        return contractNumber;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessContract(Long contractId, String userId) {
        // TODO: Implement role-based access control
        // For now, return true - will be implemented in security phase
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractHistoryDTO> getContractHistory(Long contractId) {
        // TODO: Implement in audit phase
        // Return empty list for now
        return List.of();
    }

    // =====================================
    // Private Helper Methods
    // =====================================

    private Contract findEntityById(Long id) {
        return contractRepository.findById(id)
                .filter(contract -> Boolean.TRUE.equals(contract.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found with ID: " + id));
    }

    private ContractType getContractType(Long contractTypeId) {
        return contractTypeRepository.findById(contractTypeId)
                .filter(type -> Boolean.TRUE.equals(type.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException("Contract type not found with ID: " + contractTypeId));
    }

    private CustomerDTO validateAndGetCustomer(String customerId) {
        try {
            return coreBankingService.getCustomerById(customerId);
        } catch (Exception e) {
            log.error("Failed to validate customer with ID: {}", customerId, e);
            throw new BusinessException("Customer not found or invalid: " + customerId);
        }
    }

    private void validateCreateContractRequest(CreateContractRequest request) {
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BusinessException("Contract title is required");
        }

        if (request.getContractTypeId() == null) {
            throw new BusinessException("Contract type is required");
        }

        if (!StringUtils.hasText(request.getCustomerId())) {
            throw new BusinessException("Customer ID is required");
        }

        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new BusinessException("Start date and end date are required");
        }

        validateContractDates(request.getStartDate(), request.getEndDate());

        if (StringUtils.hasText(request.getContractNumber()) &&
                !isContractNumberUnique(request.getContractNumber())) {
            throw new BusinessException("Contract number already exists: " + request.getContractNumber());
        }
    }

    private void validateContractForActivation(Contract contract) {
        if (!StringUtils.hasText(contract.getTitle())) {
            throw new BusinessException("Contract title is required for activation");
        }

        if (contract.getContractType() == null) {
            throw new BusinessException("Contract type is required for activation");
        }

        if (!StringUtils.hasText(contract.getCustomerId())) {
            throw new BusinessException("Customer is required for activation");
        }

        if (contract.getStartDate() == null || contract.getEndDate() == null) {
            throw new BusinessException("Contract dates are required for activation");
        }
    }

    private String determineSourceSystem(CustomerDTO customer) {
        if (StringUtils.hasText(customer.getT24CustomerId())) {
            return "T24";
        }
        if (StringUtils.hasText(customer.getSourceSystem())) {
            return customer.getSourceSystem();
        }
        return "MANUAL";
    }

    private String appendNote(String existingNotes, String newNote) {
        if (!StringUtils.hasText(existingNotes)) {
            return newNote + " (" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ")";
        }
        return existingNotes + "\n" + newNote + " (" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ")";
    }
}