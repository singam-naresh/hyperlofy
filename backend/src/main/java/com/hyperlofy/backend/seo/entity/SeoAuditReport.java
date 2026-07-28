package com.hyperlofy.backend.seo.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seo_audit_reports")
@SQLDelete(sql = "UPDATE seo_audit_reports SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoAuditReport extends BaseEntity {

    @Column(name = "audit_code", nullable = false, unique = true, length = 100)
    private String auditCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private SeoPage page;

    @Builder.Default
    @Column(name = "health_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal healthScore = new BigDecimal("96.50");

    @Column(name = "ai_recommendations", nullable = false, columnDefinition = "TEXT")
    private String aiRecommendations;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED";

    @Column(name = "tenant_id")
    private UUID tenantId;
}
