package com.hyperlofy.backend.experience.repository;

import com.hyperlofy.backend.experience.entity.CustomerReputation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerReputationRepository extends JpaRepository<CustomerReputation, UUID> {
    Optional<CustomerReputation> findByCustomerId(UUID customerId);
}
