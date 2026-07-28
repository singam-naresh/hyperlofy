package com.hyperlofy.backend.pickupdrop.repository;

import com.hyperlofy.backend.pickupdrop.entity.PickupDropInsuranceClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PickupDropInsuranceClaimRepository extends JpaRepository<PickupDropInsuranceClaim, UUID> {
    List<PickupDropInsuranceClaim> findByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
