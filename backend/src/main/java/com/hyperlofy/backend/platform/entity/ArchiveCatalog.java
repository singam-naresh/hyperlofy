package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "archive_catalog")
@SQLDelete(sql = "UPDATE archive_catalog SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveCatalog extends BaseEntity {

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName;

    @Column(name = "archive_location", nullable = false, length = 255)
    private String archiveLocation;

    @Builder.Default
    @Column(name = "record_count", nullable = false)
    private Integer recordCount = 0;

    @Builder.Default
    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes = 0L;

    @Column(name = "checksum_sha256", nullable = false, length = 64)
    private String checksumSha256;

    @Builder.Default
    @Column(name = "encryption_algorithm", length = 30)
    private String encryptionAlgorithm = "AES-256";

    @Builder.Default
    @Column(name = "compression_method", length = 30)
    private String compressionMethod = "GZIP";

    @Builder.Default
    @Column(name = "storage_tier", length = 30)
    private String storageTier = "COLD";

    @Builder.Default
    @Column(name = "has_legal_hold")
    private Boolean hasLegalHold = false;

    @Builder.Default
    @Column(name = "is_verified")
    private Boolean isVerified = true;
}
