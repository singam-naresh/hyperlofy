package com.hyperlofy.backend.unifiedorder.entity;

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
@Table(name = "order_outbox")
@SQLDelete(sql = "UPDATE order_outbox SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderOutbox extends BaseEntity {

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Builder.Default
    @Column(name = "is_published")
    private Boolean isPublished = false;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    @Builder.Default
    @Column(name = "retry_count")
    private Integer retryCount = 0;
}
