package com.hyperlofy.backend.engagement.repository;

import com.hyperlofy.backend.engagement.entity.CustomerBehaviourProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerBehaviourProfileRepository extends JpaRepository<CustomerBehaviourProfile, UUID> {
    Optional<CustomerBehaviourProfile> findByCustomerId(UUID customerId);
}
