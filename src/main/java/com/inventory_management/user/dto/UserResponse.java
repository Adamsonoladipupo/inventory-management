package com.inventory_management.user.dto;

import com.inventory_management.user.UserRole;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role,
        boolean active,
        Instant createdAt
) {
}
