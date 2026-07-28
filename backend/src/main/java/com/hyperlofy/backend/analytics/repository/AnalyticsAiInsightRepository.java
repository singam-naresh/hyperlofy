package com.hyperlofy.backend.analytics.repository;

import com.hyperlofy.backend.analytics.entity.AnalyticsAiInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsAiInsightRepository extends JpaRepository<AnalyticsAiInsight, UUID> {
    List<AnalyticsAiInsight> findByInsightCategoryOrderByCreatedAtDesc(String insightCategory);
}
