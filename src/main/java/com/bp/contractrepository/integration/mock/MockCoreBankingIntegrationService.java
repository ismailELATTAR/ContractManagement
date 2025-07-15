package com.bp.contractrepository.integration.mock;

import com.bp.contractrepository.customer.dto.CustomerDTO;
import com.bp.contractrepository.integration.common.CoreBankingIntegrationService;
import com.bp.contractrepository.shared.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Mock Core Banking Integration Service
 * Used for development and testing when T24/Evolan systems are not available
 *
 * @author Contract Management Team
 */
@Service
@Slf4j
@ConditionalOnProperty(
        name = "app.integration.core-banking.type",
        havingValue = "mock",
        matchIfMissing = true
)
public class MockCoreBankingIntegrationService implements CoreBankingIntegrationService {

    // Mock customer data for development
    private static final Map<String, CustomerDTO> MOCK_CUSTOMERS = Map.of(
            "CUS-12345", CustomerDTO.builder()
                    .customerId("CUS-12345")
                    .customerName("Microsoft Maroc SARL")
                    .customerType("CORPORATE")
                    .contactPerson("Ahmed Benjelloun")
                    .contactEmail("ahmed.benjelloun@microsoft.ma")
                    .contactPhone("+212 522 123 456")
                    .address("Twin Center, Boulevard Zerktouni")
                    .city("Casablanca")
                    .country("Morocco")
                    .relationshipManager("Fatima El Alami")
                    .accountManager("Youssef Sekkouri")
                    .t24CustomerId("T24-CUS-001")
                    .sourceSystem("MOCK")
                    .riskRating("LOW")
                    .sector("TECHNOLOGY")
                    .taxId("123456789")
                    .isActive(true)
                    .lastSyncDate(LocalDateTime.now())
                    .build(),

            "CUS-67890", CustomerDTO.builder()
                    .customerId("CUS-67890")
                    .customerName("OCP Group")
                    .customerType("CORPORATE")
                    .contactPerson("Youssef Sekkouri")
                    .contactEmail("y.sekkouri@ocpgroup.ma")
                    .contactPhone("+212 537 680 000")
                    .address("Hay Riad")
                    .city("Rabat")
                    .country("Morocco")
                    .relationshipManager("Rachid Ouali")
                    .accountManager("Marie Hassan")
                    .t24CustomerId("T24-CUS-002")
                    .sourceSystem("MOCK")
                    .riskRating("MEDIUM")
                    .sector("MINING")
                    .taxId("987654321")
                    .isActive(true)
                    .lastSyncDate(LocalDateTime.now())
                    .build(),

            "CUS-11111", CustomerDTO.builder()
                    .customerId("CUS-11111")
                    .customerName("Attijariwafa Bank")
                    .customerType("CORPORATE")
                    .contactPerson("Nabil Benabdellah")
                    .contactEmail("n.benabdellah@attijariwafa.ma")
                    .contactPhone("+212 522 477 474")
                    .address("2, Boulevard Moulay Youssef")
                    .city("Casablanca")
                    .country("Morocco")
                    .relationshipManager("Aicha Bennani")
                    .accountManager("Omar Fassi")
                    .t24CustomerId("T24-CUS-003")
                    .sourceSystem("MOCK")
                    .riskRating("LOW")
                    .sector("BANKING")
                    .taxId("555666777")
                    .isActive(true)
                    .lastSyncDate(LocalDateTime.now())
                    .build()
    );

    @Override
    public CustomerDTO getCustomerById(String customerId) {
        log.info("Mock: Getting customer by ID: {}", customerId);

        CustomerDTO customer = MOCK_CUSTOMERS.get(customerId);
        if (customer == null) {
            log.warn("Mock: Customer not found: {}", customerId);
            throw BusinessException.customerNotFound(customerId);
        }

        log.info("Mock: Found customer: {}", customer.getCustomerName());
        return customer;
    }

    @Override
    public List<CustomerDTO> searchCustomersByName(String customerName) {
        log.info("Mock: Searching customers by name: {}", customerName);

        List<CustomerDTO> results = MOCK_CUSTOMERS.values().stream()
                .filter(customer -> customer.getCustomerName().toLowerCase()
                        .contains(customerName.toLowerCase()))
                .toList();

        log.info("Mock: Found {} customers matching '{}'", results.size(), customerName);
        return results;
    }

    @Override
    public boolean isCustomerValid(String customerId) {
        log.info("Mock: Validating customer: {}", customerId);

        CustomerDTO customer = MOCK_CUSTOMERS.get(customerId);
        boolean isValid = customer != null && Boolean.TRUE.equals(customer.getIsActive());

        log.info("Mock: Customer {} is {}", customerId, isValid ? "valid" : "invalid");
        return isValid;
    }

    @Override
    public List<AccountSummaryDTO> getCustomerAccounts(String customerId) {
        log.info("Mock: Getting accounts for customer: {}", customerId);

        if (!MOCK_CUSTOMERS.containsKey(customerId)) {
            throw BusinessException.customerNotFound(customerId);
        }

        // Mock account data
        List<AccountSummaryDTO> accounts = List.of(
                new AccountSummaryDTO(
                        "ACC-" + customerId + "-001",
                        "001234567890",
                        "CURRENT",
                        "MAD",
                        new BigDecimal("125000.00"),
                        "ACTIVE"
                ),
                new AccountSummaryDTO(
                        "ACC-" + customerId + "-002",
                        "001234567891",
                        "SAVINGS",
                        "MAD",
                        new BigDecimal("500000.00"),
                        "ACTIVE"
                )
        );

        log.info("Mock: Found {} accounts for customer {}", accounts.size(), customerId);
        return accounts;
    }

    @Override
    public CustomerDTO refreshCustomerData(String customerId) {
        log.info("Mock: Refreshing customer data: {}", customerId);

        CustomerDTO customer = getCustomerById(customerId);

        // Update last sync date
        return CustomerDTO.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerType(customer.getCustomerType())
                .contactPerson(customer.getContactPerson())
                .contactEmail(customer.getContactEmail())
                .contactPhone(customer.getContactPhone())
                .address(customer.getAddress())
                .city(customer.getCity())
                .country(customer.getCountry())
                .relationshipManager(customer.getRelationshipManager())
                .accountManager(customer.getAccountManager())
                .t24CustomerId(customer.getT24CustomerId())
                .sourceSystem("MOCK")
                .riskRating(customer.getRiskRating())
                .sector(customer.getSector())
                .taxId(customer.getTaxId())
                .isActive(customer.getIsActive())
                .lastSyncDate(LocalDateTime.now()) // Updated sync time
                .build();
    }

    @Override
    public String getSystemName() {
        return "MOCK";
    }

    @Override
    public boolean isSystemAvailable() {
        return true; // Mock system is always available
    }

    @Override
    public SystemHealthDTO getSystemHealth() {
        return new SystemHealthDTO(
                "MOCK",
                true,
                "HEALTHY",
                5L, // 5ms response time
                LocalDateTime.now().toString(),
                "1.0.0-MOCK"
        );
    }

    @Override
    public List<CustomerDTO> bulkRefreshCustomerData(List<String> customerIds) {
        log.info("Mock: Bulk refreshing {} customers", customerIds.size());

        return customerIds.stream()
                .map(this::refreshCustomerData)
                .toList();
    }

    @Override
    public List<String> getCustomersNeedingRefresh(int daysSinceLastSync) {
        log.info("Mock: Finding customers needing refresh (older than {} days)", daysSinceLastSync);

        LocalDateTime threshold = LocalDateTime.now().minusDays(daysSinceLastSync);

        return MOCK_CUSTOMERS.values().stream()
                .filter(customer -> customer.getLastSyncDate() == null ||
                        customer.getLastSyncDate().isBefore(threshold))
                .map(CustomerDTO::getCustomerId)
                .toList();
    }
}