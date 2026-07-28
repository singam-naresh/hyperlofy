package com.hyperlofy.backend.analytics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "analytics_scorecards")
@SQLDelete(sql = "UPDATE analytics_scorecards SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsScorecard extends BaseEntity {

    @Column(name = "scorecard_role", nullable = false, unique = true, length = 50)
    private String scorecardRole; // CEO, COO, CFO, OPERATIONS, MERCHANT_SUCCESS

    @Builder.Default
    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore = new BigDecimal("95.00");

    @Builder.Default
    @Column(name = "grade", nullable = false, length = 5)
    private String grade = "A+";

    @Column(name = "metrics_summary_json")
    private String metricsSummaryJson;
}
