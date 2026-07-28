package com.hyperlofy.backend.unifiedorder.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "order_inbox")
@SQLDelete(sql = "UPDATE order_inbox SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderInbox extends BaseEntity {

    @Column(name = "message_id", nullable = false, unique = true, length = 128)
    private String messageId;

    @Column(name = "source_service", nullable = false, length = 50)
    private String sourceService;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Builder.Default
    @Column(name = "is_processed")
    private Boolean isProcessed = true;

    @Builder.Default
    @Column(name = "processed_at")
    private ZonedDateTime processedAt = ZonedDateTime.now();
}
