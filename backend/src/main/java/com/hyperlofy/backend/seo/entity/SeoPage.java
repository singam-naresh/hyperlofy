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
@Table(name = "seo_pages")
@SQLDelete(sql = "UPDATE seo_pages SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoPage extends BaseEntity {

    @Column(name = "page_url", nullable = false, unique = true, length = 500)
    private String pageUrl;

    @Column(name = "meta_title", nullable = false, length = 255)
    private String metaTitle;

    @Column(name = "meta_description", nullable = false, columnDefinition = "TEXT")
    private String metaDescription;

    @Column(name = "canonical_url", nullable = false, length = 500)
    private String canonicalUrl;

    @Column(name = "open_graph_title", length = 255)
    private String openGraphTitle;

    @Column(name = "open_graph_image", length = 500)
    private String openGraphImage;

    @Builder.Default
    @Column(name = "is_indexable", nullable = false)
    private Boolean isIndexable = true;

    @Builder.Default
    @Column(name = "seo_health_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal seoHealthScore = new BigDecimal("95.00");

    @Column(name = "tenant_id")
    private UUID tenantId;
}
