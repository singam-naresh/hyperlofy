package com.hyperlofy.backend.seo.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "seo_landing_pages")
@SQLDelete(sql = "UPDATE seo_landing_pages SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoLandingPage extends BaseEntity {

    @Column(name = "landing_code", nullable = false, unique = true, length = 100)
    private String landingCode;

    @Column(name = "city_name", nullable = false, length = 100)
    private String cityName;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    @Column(name = "target_keyword", nullable = false, length = 150)
    private String targetKeyword;

    @Column(name = "page_path", nullable = false, unique = true, length = 255)
    private String pagePath;

    @Builder.Default
    @Column(name = "monthly_organic_views", nullable = false)
    private Integer monthlyOrganicViews = 12500;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
