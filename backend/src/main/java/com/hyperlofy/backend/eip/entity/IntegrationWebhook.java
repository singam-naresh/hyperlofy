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
@Table(name = "integration_webhooks")
@SQLDelete(sql = "UPDATE integration_webhooks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationWebhook extends BaseEntity {

    @Column(name = "connector_id", nullable = false)
    private UUID connectorId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload_hash", nullable = false, length = 100)
    private String payloadHash;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "PROCESSED";

    @Builder.Default
    @Column(name = "processed_at")
    private OffsetDateTime processedAt = OffsetDateTime.now();
}
