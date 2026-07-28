package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsScorecard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalyticsScorecardRepository extends JpaRepository<AnalyticsScorecard, UUID> {
    Optional<AnalyticsScorecard> findByScorecardRole(String scorecardRole);
}
