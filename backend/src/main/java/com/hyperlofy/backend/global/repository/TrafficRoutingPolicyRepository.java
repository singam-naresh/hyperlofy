package com.hyperlofy.backend.global.repository;

import com.hyperlofy.backend.global.entity.TrafficRoutingPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TrafficRoutingPolicyRepository extends JpaRepository<TrafficRoutingPolicy, UUID> {
    Optional<TrafficRoutingPolicy> findByPolicyName(String policyName);
    List<TrafficRoutingPolicy> findByTargetRegionCode(String targetRegionCode);
}
