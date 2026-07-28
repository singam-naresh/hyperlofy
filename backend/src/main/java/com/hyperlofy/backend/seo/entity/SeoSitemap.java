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
@Table(name = "seo_sitemaps")
@SQLDelete(sql = "UPDATE seo_sitemaps SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoSitemap extends BaseEntity {

    @Column(name = "sitemap_code", nullable = false, unique = true, length = 100)
    private String sitemapCode;

    @Builder.Default
    @Column(name = "sitemap_type", nullable = false, length = 50)
    private String sitemapType = "PRODUCT"; // PRODUCT, MERCHANT, CATEGORY, BRAND

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Builder.Default
    @Column(name = "total_urls", nullable = false)
    private Integer totalUrls = 10000;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PUBLISHED"; // DRAFT, REGENERATING, PUBLISHED

    @Column(name = "tenant_id")
    private UUID tenantId;
}
