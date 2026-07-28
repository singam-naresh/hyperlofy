package com.hyperlofy.backend.multitenancy.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "tenant_branding")
@SQLDelete(sql = "UPDATE tenant_branding SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantBranding extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;

    @Builder.Default
    @Column(name = "primary_color", nullable = false, length = 30)
    private String primaryColor = "#6200EE";

    @Builder.Default
    @Column(name = "secondary_color", nullable = false, length = 30)
    private String secondaryColor = "#03DAC6";

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "custom_css")
    private String customCss;
}
