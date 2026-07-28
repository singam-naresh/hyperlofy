package com.hyperlofy.backend.seo.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "seo_keyword_rankings")
@SQLDelete(sql = "UPDATE seo_keyword_rankings SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoKeywordRanking extends BaseEntity {

    @Column(name = "keyword", nullable = false, unique = true, length = 150)
    private String keyword;

    @Builder.Default
    @Column(name = "current_rank", nullable = false)
    private Integer currentRank = 1;

    @Builder.Default
    @Column(name = "previous_rank", nullable = false)
    private Integer previousRank = 2;

    @Builder.Default
    @Column(name = "monthly_search_volume", nullable = false)
    private Integer monthlySearchVolume = 45000;

    @Builder.Default
    @Column(name = "click_through_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal clickThroughRate = new BigDecimal("18.50");

    @Column(name = "tenant_id")
    private UUID tenantId;
}
