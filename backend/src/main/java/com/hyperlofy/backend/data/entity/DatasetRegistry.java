package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "dataset_registry")
@SQLDelete(sql = "UPDATE dataset_registry SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatasetRegistry extends BaseEntity {

    @Column(name = "dataset_name", nullable = false, unique = true, length = 150)
    private String datasetName;

    @Column(name = "dataset_owner", nullable = false, length = 100)
    private String datasetOwner;

    @Builder.Default
    @Column(name = "classification_level", nullable = false, length = 30)
    private String classificationLevel = "CONFIDENTIAL"; // PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED

    @Builder.Default
    @Column(name = "certification_status", nullable = false, length = 30)
    private String certificationStatus = "CERTIFIED"; // UNVERIFIED, CERTIFIED, DEPRECATED

    @Builder.Default
    @Column(name = "quality_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal qualityScore = new BigDecimal("99.50");
}
