package com.hyperlofy.backend.matching.repository;

import com.hyperlofy.backend.matching.entity.MatchingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchingAssignmentRepository extends JpaRepository<MatchingAssignment, UUID> {
    List<MatchingAssignment> findByMatchingRequestId(UUID matchingRequestId);
    Optional<MatchingAssignment> findByMatchingRequestIdAndDriverId(UUID matchingRequestId, UUID driverId);
}
