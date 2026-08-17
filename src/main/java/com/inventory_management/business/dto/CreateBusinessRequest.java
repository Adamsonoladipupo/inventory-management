package com.inventory_management.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBusinessRequest(

        @NotBlank(message = "Business name is required")
        @Size(max = 150, message = "Business name must not exceed 150 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be a valid email address")
        @Size(max = 150, message = "Email must not exceed 150 characters")
        String email,

        @NotBlank(message = "Phone number is required")
        @Size(max = 30, message = "Phone number must not exceed 30 characters")
        String phone,

        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        String address
) {
}
