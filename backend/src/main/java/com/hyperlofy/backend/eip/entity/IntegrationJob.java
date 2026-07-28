package com.hyperlofy.backend.eip.entity;

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
@Table(name = "integration_jobs")
@SQLDelete(sql = "UPDATE integration_jobs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationJob extends BaseEntity {

    @Column(name = "connector_id", nullable = false)
    private UUID connectorId;

    @Column(name = "job_type", nullable = false, length = 100)
    private String jobType; // INVENTORY_SYNC, INVOICE_EXPORT, ORDER_SYNC

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // RUNNING, COMPLETED, FAILED

    @Builder.Default
    @Column(name = "records_processed", nullable = false)
    private Integer recordsProcessed = 0;

    @Builder.Default
    @Column(name = "records_failed", nullable = false)
    private Integer recordsFailed = 0;

    @Builder.Default
    @Column(name = "started_at")
    private OffsetDateTime startedAt = OffsetDateTime.now();

    @Builder.Default
    @Column(name = "completed_at")
    private OffsetDateTime completedAt = OffsetDateTime.now();
}
