package com.hyperlofy.backend.matching.repository;

import com.hyperlofy.backend.matching.entity.MatchingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchingRequestRepository extends JpaRepository<MatchingRequest, UUID> {
    Optional<MatchingRequest> findByOrderId(UUID orderId);
}
