package com.hyperlofy.backend.support.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "return_cases")
@SQLDelete(sql = "UPDATE return_cases SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnCase extends BaseEntity {

    @Column(name = "return_code", nullable = false, unique = true, length = 100)
    private String returnCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Builder.Default
    @Column(name = "pickup_status", nullable = false, length = 50)
    private String pickupStatus = "SCHEDULED"; // SCHEDULED, PICKED_UP, INSPECTED, REJECTED, COMPLETED

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REQUESTED";

    @Column(name = "tenant_id")
    private UUID tenantId;
}
