package com.inventory_management.user.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A user with this email already exists: " + email);
    }
}
