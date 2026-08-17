package com.inventory_management.business.exception;

import com.inventory_management.common.exception.NotFoundException;

import java.util.UUID;

public class BusinessNotFoundException extends NotFoundException {

    public BusinessNotFoundException(UUID id) {
        super("Business not found with id: " + id);
    }
}
