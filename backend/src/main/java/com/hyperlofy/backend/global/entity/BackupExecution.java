package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;

@Entity
@Table(name = "backup_executions")
@SQLDelete(sql = "UPDATE backup_executions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupExecution extends BaseEntity {

    @Column(name = "backup_code", nullable = false, unique = true, length = 100)
    private String backupCode;

    @Column(name = "region_code", nullable = false, length = 50)
    private String regionCode;

    @Builder.Default
    @Column(name = "backup_type", nullable = false, length = 30)
    private String backupType = "FULL"; // FULL, INCREMENTAL, SNAPSHOT, WAL

    @Builder.Default
    @Column(name = "storage_size_bytes", nullable = false)
    private Long storageSizeBytes = 0L;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // COMPLETED, IN_PROGRESS, FAILED

    @Column(name = "s3_snapshot_uri", nullable = false, length = 500)
    private String s3SnapshotUri;

    @Builder.Default
    @Column(name = "completed_at")
    private OffsetDateTime completedAt = OffsetDateTime.now();
}
