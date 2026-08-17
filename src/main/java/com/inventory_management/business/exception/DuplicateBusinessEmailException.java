package com.inventory_management.business.exception;

import com.inventory_management.common.exception.ConflictException;

public class DuplicateBusinessEmailException extends ConflictException {

    public DuplicateBusinessEmailException(String email) {
        super("A business with this email already exists: " + email);
    }
}
