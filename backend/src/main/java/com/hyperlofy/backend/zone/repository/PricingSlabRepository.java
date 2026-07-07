package com.hyperlofy.backend.zone.repository;

import com.hyperlofy.backend.zone.entity.PricingSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PricingSlabRepository extends JpaRepository<PricingSlab, UUID> {
    List<PricingSlab> findByZoneIdOrderByMinDistanceKmAsc(UUID zoneId);
}
