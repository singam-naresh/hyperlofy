package com.hyperlofy.backend.customer.repository;

import com.hyperlofy.backend.customer.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerProfile, UUID> {
    Optional<CustomerProfile> findByUserId(UUID userId);
}
