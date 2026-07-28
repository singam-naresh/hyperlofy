package com.hyperlofy.backend.matching.repository;

import com.hyperlofy.backend.matching.entity.MatchingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MatchingReservationRepository extends JpaRepository<MatchingReservation, UUID> {
    Optional<MatchingReservation> findByOrderId(UUID orderId);
}
