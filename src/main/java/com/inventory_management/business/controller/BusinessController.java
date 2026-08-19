package com.inventory_management.business.controller;

import com.inventory_management.business.dto.BusinessResponse;
import com.inventory_management.business.dto.CreateBusinessRequest;
import com.inventory_management.business.dto.UpdateBusinessRequest;
import com.inventory_management.business.service.BusinessService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping
    public ResponseEntity<BusinessResponse> createBusiness(@Valid @RequestBody CreateBusinessRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(businessService.createBusiness(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessResponse> getBusinessById(@PathVariable UUID id) {
        return ResponseEntity.ok(businessService.getBusinessById(id));
    }

    @GetMapping
    public ResponseEntity<List<BusinessResponse>> getAllBusinesses() {
        return ResponseEntity.ok(businessService.getAllBusinesses());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessResponse> updateBusiness(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBusinessRequest request) {
        return ResponseEntity.ok(businessService.updateBusiness(id, request));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<BusinessResponse> activateBusiness(@PathVariable UUID id) {
        return ResponseEntity.ok(businessService.activateBusiness(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<BusinessResponse> deactivateBusiness(@PathVariable UUID id) {
        return ResponseEntity.ok(businessService.deactivateBusiness(id));
    }

}
