package com.hyperlofy.backend.seo.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "seo_structured_data")
@SQLDelete(sql = "UPDATE seo_structured_data SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeoStructuredData extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "page_id", nullable = false)
    private SeoPage page;

    @Builder.Default
    @Column(name = "schema_type", nullable = false, length = 80)
    private String schemaType = "PRODUCT"; // PRODUCT, LOCAL_BUSINESS, BREADCRUMB, FAQ, REVIEW

    @Column(name = "json_ld_content", nullable = false, columnDefinition = "TEXT")
    private String jsonLdContent;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
