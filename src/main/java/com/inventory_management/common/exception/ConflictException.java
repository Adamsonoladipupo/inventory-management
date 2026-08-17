package com.inventory_management.common.exception;

/**
 * Base class for 409 Conflict exceptions.
 * Domain-specific conflict exceptions should extend this class.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
