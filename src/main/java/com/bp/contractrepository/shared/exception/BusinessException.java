package com.bp.contractrepository.shared.exception;

/**
 * Business Exception
 * Thrown when business rules are violated or business logic fails
 *
 * Examples:
 * - Contract dates validation fails
 * - Customer not found in core banking
 * - Status transition not allowed
 * - Contract cannot be deleted due to business rules
 *
 * @author Contract Management Team
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final Object[] parameters;

    /**
     * Constructor with message only
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
        this.parameters = new Object[0];
    }

    /**
     * Constructor with message and cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
        this.parameters = new Object[0];
    }

    /**
     * Constructor with error code and message
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }

    /**
     * Constructor with error code, message and cause
     */
    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = new Object[0];
    }

    /**
     * Constructor with error code, message and parameters
     */
    public BusinessException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters != null ? parameters : new Object[0];
    }

    /**
     * Constructor with error code, message, cause and parameters
     */
    public BusinessException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters != null ? parameters : new Object[0];
    }

    /**
     * Get error code for programmatic handling
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get parameters for message formatting
     */
    public Object[] getParameters() {
        return parameters.clone();
    }

    /**
     * Check if this exception has a specific error code
     */
    public boolean hasErrorCode(String code) {
        return errorCode != null && errorCode.equals(code);
    }

    // =====================================
    // Static Factory Methods for Common Business Errors
    // =====================================

    /**
     * Contract validation errors
     */
    public static BusinessException invalidContractDates(String startDate, String endDate) {
        return new BusinessException("INVALID_CONTRACT_DATES",
                "Contract start date (" + startDate + ") must be before end date (" + endDate + ")",
                startDate, endDate);
    }

    public static BusinessException contractNumberExists(String contractNumber) {
        return new BusinessException("CONTRACT_NUMBER_EXISTS",
                "Contract number already exists: " + contractNumber,
                contractNumber);
    }

    public static BusinessException contractNotEditable(String status) {
        return new BusinessException("CONTRACT_NOT_EDITABLE",
                "Contract with status '" + status + "' cannot be edited",
                status);
    }

    public static BusinessException contractNotDeletable(String status) {
        return new BusinessException("CONTRACT_NOT_DELETABLE",
                "Contract with status '" + status + "' cannot be deleted",
                status);
    }

    /**
     * Customer validation errors
     */
    public static BusinessException customerNotFound(String customerId) {
        return new BusinessException("CUSTOMER_NOT_FOUND",
                "Customer not found in core banking system: " + customerId,
                customerId);
    }

    public static BusinessException customerInactive(String customerId) {
        return new BusinessException("CUSTOMER_INACTIVE",
                "Customer is inactive in core banking system: " + customerId,
                customerId);
    }

    /**
     * Status transition errors
     */
    public static BusinessException invalidStatusTransition(String fromStatus, String toStatus) {
        return new BusinessException("INVALID_STATUS_TRANSITION",
                "Cannot change contract status from '" + fromStatus + "' to '" + toStatus + "'",
                fromStatus, toStatus);
    }

    public static BusinessException contractNotActivatable(String reason) {
        return new BusinessException("CONTRACT_NOT_ACTIVATABLE",
                "Contract cannot be activated: " + reason,
                reason);
    }

    /**
     * Financial validation errors
     */
    public static BusinessException invalidContractValue(String value) {
        return new BusinessException("INVALID_CONTRACT_VALUE",
                "Contract value must be positive: " + value,
                value);
    }

    public static BusinessException currencyNotSupported(String currency) {
        return new BusinessException("CURRENCY_NOT_SUPPORTED",
                "Currency not supported: " + currency,
                currency);
    }

    /**
     * Integration errors
     */
    public static BusinessException coreBankingUnavailable(String system) {
        return new BusinessException("CORE_BANKING_UNAVAILABLE",
                "Core banking system unavailable: " + system,
                system);
    }

    public static BusinessException customerSyncFailed(String customerId, String reason) {
        return new BusinessException("CUSTOMER_SYNC_FAILED",
                "Failed to sync customer data for " + customerId + ": " + reason,
                customerId, reason);
    }

    /**
     * Authorization errors
     */
    public static BusinessException accessDenied(String resource, String action) {
        return new BusinessException("ACCESS_DENIED",
                "Access denied for action '" + action + "' on resource '" + resource + "'",
                resource, action);
    }

    public static BusinessException insufficientPermissions(String requiredRole) {
        return new BusinessException("INSUFFICIENT_PERMISSIONS",
                "Insufficient permissions. Required role: " + requiredRole,
                requiredRole);
    }

    /**
     * Document errors
     */
    public static BusinessException documentNotFound(String documentId) {
        return new BusinessException("DOCUMENT_NOT_FOUND",
                "Document not found: " + documentId,
                documentId);
    }

    public static BusinessException invalidFileType(String fileName, String allowedTypes) {
        return new BusinessException("INVALID_FILE_TYPE",
                "Invalid file type for '" + fileName + "'. Allowed types: " + allowedTypes,
                fileName, allowedTypes);
    }

    public static BusinessException fileSizeExceeded(String fileName, long maxSize) {
        return new BusinessException("FILE_SIZE_EXCEEDED",
                "File size exceeded for '" + fileName + "'. Maximum size: " + maxSize + " bytes",
                fileName, maxSize);
    }

    /**
     * Workflow errors
     */
    public static BusinessException approvalRequired(String contractType) {
        return new BusinessException("APPROVAL_REQUIRED",
                "Contract type '" + contractType + "' requires approval before activation",
                contractType);
    }

    public static BusinessException workflowStepFailed(String step, String reason) {
        return new BusinessException("WORKFLOW_STEP_FAILED",
                "Workflow step '" + step + "' failed: " + reason,
                step, reason);
    }

    /**
     * Data integrity errors
     */
    public static BusinessException concurrentModification(String entityType, Object entityId) {
        return new BusinessException("CONCURRENT_MODIFICATION",
                "Entity '" + entityType + "' with ID '" + entityId + "' was modified by another user",
                entityType, entityId);
    }

    public static BusinessException referentialIntegrity(String entityType, Object entityId, String referencedBy) {
        return new BusinessException("REFERENTIAL_INTEGRITY",
                "Cannot delete '" + entityType + "' with ID '" + entityId + "' because it is referenced by: " + referencedBy,
                entityType, entityId, referencedBy);
    }

    /**
     * Lifecycle errors
     */
    public static BusinessException contractExpired(String contractNumber, String expiryDate) {
        return new BusinessException("CONTRACT_EXPIRED",
                "Contract '" + contractNumber + "' expired on " + expiryDate,
                contractNumber, expiryDate);
    }

    public static BusinessException renewalNotAllowed(String contractType) {
        return new BusinessException("RENEWAL_NOT_ALLOWED",
                "Auto-renewal is not allowed for contract type: " + contractType,
                contractType);
    }

    @Override
    public String toString() {
        return String.format("BusinessException{errorCode='%s', message='%s', parameters=%s}",
                errorCode, getMessage(), java.util.Arrays.toString(parameters));
    }
}