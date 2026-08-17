package com.inventory_management.user.exception;

import com.inventory_management.common.exception.NotFoundException;

import java.util.UUID;

public class UserNotFoundException extends NotFoundException {

    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }
}
