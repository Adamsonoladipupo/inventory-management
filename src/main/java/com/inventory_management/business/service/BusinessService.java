package com.inventory_management.business.service;

import com.inventory_management.business.dto.BusinessResponse;
import com.inventory_management.business.dto.CreateBusinessRequest;
import com.inventory_management.business.dto.UpdateBusinessRequest;

import java.util.List;
import java.util.UUID;

public interface BusinessService {

    BusinessResponse createBusiness(CreateBusinessRequest request);

    BusinessResponse getBusinessById(UUID id);

    List<BusinessResponse> getAllBusinesses();

    BusinessResponse updateBusiness(UUID id, UpdateBusinessRequest request);

    BusinessResponse activateBusiness(UUID id);

    BusinessResponse deactivateBusiness(UUID id);
}
