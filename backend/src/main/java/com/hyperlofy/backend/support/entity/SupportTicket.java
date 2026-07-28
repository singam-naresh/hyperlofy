package com.hyperlofy.backend.support.entity;

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
@Table(name = "support_tickets")
@SQLDelete(sql = "UPDATE support_tickets SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicket extends BaseEntity {

    @Column(name = "ticket_code", nullable = false, unique = true, length = 100)
    private String ticketCode;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "order_id")
    private UUID orderId;

    @Builder.Default
    @Column(name = "category", nullable = false, length = 80)
    private String category = "REFUND"; // REFUND, RETURN, REPLACEMENT, MISSING_PRODUCT, WRONG_PRODUCT, DAMAGED_PRODUCT, DELIVERY_DELAY

    @Builder.Default
    @Column(name = "priority", nullable = false, length = 30)
    private String priority = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "OPEN"; // OPEN, ASSIGNED, IN_PROGRESS, WAITING_CUSTOMER, ESCALATED, RESOLVED, CLOSED

    @Column(name = "assigned_agent_id")
    private UUID assignedAgentId;

    @Column(name = "sla_due_time")
    private OffsetDateTime slaDueTime;

    @Builder.Default
    @Column(name = "is_sla_breached", nullable = false)
    private Boolean isSlaBreached = false;

    @Column(name = "tenant_id")
    private UUID tenantId;
}
