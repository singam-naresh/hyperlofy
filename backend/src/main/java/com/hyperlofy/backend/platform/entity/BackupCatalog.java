package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "backup_catalog")
@SQLDelete(sql = "UPDATE backup_catalog SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupCatalog extends BaseEntity {

    @Column(name = "backup_job_id")
    private UUID backupJobId;

    @Column(name = "backup_type", nullable = false, length = 30)
    private String backupType;

    @Column(name = "storage_location", nullable = false, length = 255)
    private String storageLocation;

    @Builder.Default
    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes = 0L;

    @Column(name = "checksum_sha256", nullable = false, length = 64)
    private String checksumSha256;

    @Builder.Default
    @Column(name = "encryption_algorithm", length = 30)
    private String encryptionAlgorithm = "AES-256";

    @Builder.Default
    @Column(name = "retention_days", nullable = false)
    private Integer retentionDays = 30;

    @Column(name = "expires_at", nullable = false)
    private ZonedDateTime expiresAt;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = true;
}
