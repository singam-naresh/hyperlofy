package com.hyperlofy.backend.eip.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "connector_marketplace")
@SQLDelete(sql = "UPDATE connector_marketplace SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectorMarketplace extends BaseEntity {

    @Column(name = "template_name", nullable = false, unique = true, length = 150)
    private String templateName;

    @Builder.Default
    @Column(name = "publisher", nullable = false, length = 100)
    private String publisher = "HYPERLOFY_LABS";

    @Column(name = "category", nullable = false, length = 50)
    private String category; // ERP, CRM, LOGISTICS, PAYMENT

    @Builder.Default
    @Column(name = "version", nullable = false, length = 30)
    private String version = "v1.0.0";

    @Builder.Default
    @Column(name = "certification_status", nullable = false, length = 30)
    private String certificationStatus = "CERTIFIED"; // PENDING, CERTIFIED, DEPRECATED

    @Builder.Default
    @Column(name = "download_count", nullable = false)
    private Integer downloadCount = 0;
}
