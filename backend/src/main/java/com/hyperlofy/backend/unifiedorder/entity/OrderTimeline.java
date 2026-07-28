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
@Table(name = "order_timeline")
@SQLDelete(sql = "UPDATE order_timeline SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimeline extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;

    @Column(name = "actor_id", nullable = false, length = 100)
    private String actorId;

    @Column(name = "actor_type", nullable = false, length = 30)
    private String actorType; // CUSTOMER, DRIVER, MERCHANT, SYSTEM, ADMIN

    @Column(name = "source_service", nullable = false, length = 50)
    private String sourceService;

    @Column(name = "event_description", columnDefinition = "TEXT")
    private String eventDescription;

    @Builder.Default
    @Column(name = "event_time")
    private ZonedDateTime eventTime = ZonedDateTime.now();
}
