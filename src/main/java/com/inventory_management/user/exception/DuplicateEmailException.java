package com.inventory_management.user.exception;

import com.inventory_management.common.exception.ConflictException;

public class DuplicateEmailException extends ConflictException {

    public DuplicateEmailException(String email) {
        super("A user with this email already exists: " + email);
    }
}
