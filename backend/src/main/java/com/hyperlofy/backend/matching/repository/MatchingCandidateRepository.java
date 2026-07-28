package com.hyperlofy.backend.matching.repository;

import com.hyperlofy.backend.matching.entity.MatchingCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchingCandidateRepository extends JpaRepository<MatchingCandidate, UUID> {
    List<MatchingCandidate> findByMatchingRequestIdOrderByRankPositionAsc(UUID matchingRequestId);
}
