package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "retention_policies")
@SQLDelete(sql = "UPDATE retention_policies SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetentionPolicy extends BaseEntity {

    @Column(name = "policy_name", nullable = false, unique = true, length = 100)
    private String policyName;

    @Column(name = "data_classification", nullable = false, length = 50)
    private String dataClassification; // TRANSACTIONAL, FINANCIAL, SECURITY, AUDIT

    @Builder.Default
    @Column(name = "retention_period_days", nullable = false)
    private Integer retentionPeriodDays = 365;

    @Builder.Default
    @Column(name = "storage_tier", nullable = false, length = 30)
    private String storageTier = "WARM"; // HOT, WARM, COLD, ARCHIVE

    @Builder.Default
    @Column(name = "auto_archive")
    private Boolean autoArchive = true;

    @Builder.Default
    @Column(name = "auto_purge")
    private Boolean autoPurge = false;
}
