package com.hyperlofy.backend.engagement.entity;

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
@Table(name = "notification_decisions")
@SQLDelete(sql = "UPDATE notification_decisions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDecision extends BaseEntity {

    @Column(name = "decision_code", nullable = false, unique = true, length = 100)
    private String decisionCode;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "trigger_event", nullable = false, length = 100)
    private String triggerEvent; // CART_ABANDONMENT, REORDER_REMINDER, PRICE_DROP, WISHLIST_STOCK

    @Builder.Default
    @Column(name = "optimal_channel", nullable = false, length = 50)
    private String optimalChannel = "PUSH_NOTIFICATION"; // PUSH_NOTIFICATION, EMAIL, SMS, WHATSAPP

    @Column(name = "optimal_delivery_time", nullable = false)
    private OffsetDateTime optimalDeliveryTime;

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 30)
    private String priority = "HIGH"; // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "decision_explanation", nullable = false, columnDefinition = "TEXT")
    private String decisionExplanation;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "QUEUED"; // QUEUED, DELIVERED, FAILED

    @Column(name = "tenant_id")
    private UUID tenantId;
}
