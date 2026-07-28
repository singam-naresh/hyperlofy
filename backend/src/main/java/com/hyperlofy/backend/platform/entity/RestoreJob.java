package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "restore_jobs")
@SQLDelete(sql = "UPDATE restore_jobs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestoreJob extends BaseEntity {

    @Column(name = "backup_catalog_id", nullable = false)
    private UUID backupCatalogId;

    @Column(name = "restore_target", nullable = false, length = 100)
    private String restoreTarget;

    @Builder.Default
    @Column(name = "restore_status", nullable = false, length = 30)
    private String restoreStatus = "COMPLETED";

    @Column(name = "initiated_by", nullable = false, length = 100)
    private String initiatedBy;

    @Builder.Default
    @Column(name = "recovery_time_seconds")
    private Integer recoveryTimeSeconds = 0;

    @Builder.Default
    @Column(name = "recovery_confidence_percentage")
    private Double recoveryConfidencePercentage = 99.9;
}
