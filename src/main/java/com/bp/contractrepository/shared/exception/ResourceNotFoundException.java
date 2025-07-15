package com.bp.contractrepository.shared.exception;

/**
 * Resource Not Found Exception
 * Thrown when a requested entity/resource cannot be found in the database
 *
 * @author Contract Management Team
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with message only
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // =====================================
    // Static Factory Methods for Common Resources
    // =====================================

    /**
     * Contract not found errors
     */
    public static ResourceNotFoundException contractNotFound(Long contractId) {
        return new ResourceNotFoundException("Contract not found with ID: " + contractId);
    }

    public static ResourceNotFoundException contractNotFoundByNumber(String contractNumber) {
        return new ResourceNotFoundException("Contract not found with number: " + contractNumber);
    }

    /**
     * Contract Type not found errors
     */
    public static ResourceNotFoundException contractTypeNotFound(Long contractTypeId) {
        return new ResourceNotFoundException("Contract type not found with ID: " + contractTypeId);
    }

    /**
     * Customer not found errors
     */
    public static ResourceNotFoundException customerNotFound(String customerId) {
        return new ResourceNotFoundException("Customer not found with ID: " + customerId);
    }

    /**
     * Document not found errors
     */
    public static ResourceNotFoundException documentNotFound(Long documentId) {
        return new ResourceNotFoundException("Document not found with ID: " + documentId);
    }

    /**
     * User not found errors
     */
    public static ResourceNotFoundException userNotFound(String userId) {
        return new ResourceNotFoundException("User not found with ID: " + userId);
    }
}