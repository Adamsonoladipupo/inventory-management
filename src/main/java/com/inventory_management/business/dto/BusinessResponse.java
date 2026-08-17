package com.inventory_management.business.dto;

import java.time.Instant;
import java.util.UUID;

public record BusinessResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String address,
        boolean active,
        Instant createdAt
) {
}
