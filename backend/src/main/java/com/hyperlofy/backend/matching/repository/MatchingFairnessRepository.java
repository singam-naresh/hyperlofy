package com.hyperlofy.backend.matching.repository;

import com.hyperlofy.backend.matching.entity.MatchingFairness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchingFairnessRepository extends JpaRepository<MatchingFairness, UUID> {
    Optional<MatchingFairness> findByDriverId(UUID driverId);
}
