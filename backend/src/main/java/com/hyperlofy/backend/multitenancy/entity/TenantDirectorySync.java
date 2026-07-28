package com.hyperlofy.backend.multitenancy.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant_directory_sync")
@SQLDelete(sql = "UPDATE tenant_directory_sync SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantDirectorySync extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Builder.Default
    @Column(name = "sync_source", nullable = false, length = 100)
    private String syncSource = "OKTA_SCIM";

    @Builder.Default
    @Column(name = "total_users_synced", nullable = false)
    private Integer totalUsersSynced = 0;

    @Builder.Default
    @Column(name = "sync_status", nullable = false, length = 30)
    private String syncStatus = "COMPLETED"; // IN_PROGRESS, COMPLETED, FAILED

    @Builder.Default
    @Column(name = "last_synced_at")
    private OffsetDateTime lastSyncedAt = OffsetDateTime.now();
}
