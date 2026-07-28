package com.hyperlofy.backend.analytics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "analytics_ai_insights")
@SQLDelete(sql = "UPDATE analytics_ai_insights SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsAiInsight extends BaseEntity {

    @Column(name = "insight_category", nullable = false, length = 100)
    private String insightCategory;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "recommendation_text", nullable = false)
    private String recommendationText;

    @Builder.Default
    @Column(name = "impact_score", nullable = false, length = 30)
    private String impactScore = "HIGH";
}
