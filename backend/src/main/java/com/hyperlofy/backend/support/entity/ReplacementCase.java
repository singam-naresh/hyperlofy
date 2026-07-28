package com.hyperlofy.backend.support.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "replacement_cases")
@SQLDelete(sql = "UPDATE replacement_cases SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReplacementCase extends BaseEntity {

    @Column(name = "replacement_code", nullable = false, unique = true, length = 100)
    private String replacementCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private SupportTicket ticket;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Builder.Default
    @Column(name = "dispatch_status", nullable = false, length = 50)
    private String dispatchStatus = "PENDING_DISPATCH";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REQUESTED";

    @Column(name = "tenant_id")
    private UUID tenantId;
}
