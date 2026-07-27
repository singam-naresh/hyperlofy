package com.hyperlofy.backend.ai.logistics.repository;

import com.hyperlofy.backend.ai.logistics.entity.EtaPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EtaPredictionRepository extends JpaRepository<EtaPrediction, UUID> {
    Optional<EtaPrediction> findTopByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
