package com.inventory_management.business.repository;

import com.inventory_management.business.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BusinessRepository extends JpaRepository<Business, UUID> {

    Optional<Business> findByEmail(String email);

    boolean existsByEmail(String email);
}
