package com.inventory_management.common.exception;

/**
 * Base class for 404 Not Found exceptions.
 * Domain-specific not-found exceptions should extend this class.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
