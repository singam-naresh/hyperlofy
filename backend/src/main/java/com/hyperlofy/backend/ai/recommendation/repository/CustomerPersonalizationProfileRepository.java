package com.hyperlofy.backend.ai.recommendation.repository;

import com.hyperlofy.backend.ai.recommendation.entity.CustomerPersonalizationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerPersonalizationProfileRepository extends JpaRepository<CustomerPersonalizationProfile, UUID> {
    Optional<CustomerPersonalizationProfile> findByUserId(UUID userId);
}
