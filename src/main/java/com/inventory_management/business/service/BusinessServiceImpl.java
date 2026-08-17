package com.inventory_management.business.service;

import com.inventory_management.business.dto.BusinessResponse;
import com.inventory_management.business.dto.CreateBusinessRequest;
import com.inventory_management.business.dto.UpdateBusinessRequest;
import com.inventory_management.business.entity.Business;
import com.inventory_management.business.exception.BusinessNotFoundException;
import com.inventory_management.business.exception.DuplicateBusinessEmailException;
import com.inventory_management.business.repository.BusinessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;

    public BusinessServiceImpl(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    @Override
    public BusinessResponse createBusiness(CreateBusinessRequest request) {
        if (businessRepository.existsByEmail(request.email())) {
            throw new DuplicateBusinessEmailException(request.email());
        }

        Business business = new Business();
        business.setName(request.name());
        business.setEmail(request.email());
        business.setPhone(request.phone());
        business.setAddress(request.address());

        return toResponse(businessRepository.save(business));
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessResponse getBusinessById(UUID id) {
        return toResponse(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessResponse> getAllBusinesses() {
        return businessRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BusinessResponse updateBusiness(UUID id, UpdateBusinessRequest request) {
        Business business = findByIdOrThrow(id);

        if (!business.getEmail().equals(request.email()) && businessRepository.existsByEmail(request.email())) {
            throw new DuplicateBusinessEmailException(request.email());
        }

        business.setName(request.name());
        business.setEmail(request.email());
        business.setPhone(request.phone());
        business.setAddress(request.address());

        return toResponse(businessRepository.save(business));
    }

    @Override
    public BusinessResponse activateBusiness(UUID id) {
        Business business = findByIdOrThrow(id);
        business.setActive(true);
        return toResponse(businessRepository.save(business));
    }

    @Override
    public BusinessResponse deactivateBusiness(UUID id) {
        Business business = findByIdOrThrow(id);
        business.setActive(false);
        return toResponse(businessRepository.save(business));
    }

    private Business findByIdOrThrow(UUID id) {
        return businessRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(id));
    }

    private BusinessResponse toResponse(Business business) {
        return new BusinessResponse(
                business.getId(),
                business.getName(),
                business.getEmail(),
                business.getPhone(),
                business.getAddress(),
                business.isActive(),
                business.getCreatedAt()
        );
    }
}
