package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "dr_failover_logs")
@SQLDelete(sql = "UPDATE dr_failover_logs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrFailoverLog extends BaseEntity {

    @Column(name = "target_system", nullable = false, length = 100)
    private String targetSystem; // DATABASE, REDIS, GATEWAY, PAYMENT_GATEWAY

    @Column(name = "old_active_node", nullable = false, length = 255)
    private String oldActiveNode;

    @Column(name = "new_active_node", nullable = false, length = 255)
    private String newActiveNode;

    @Column(name = "failover_reason", nullable = false, length = 255)
    private String failoverReason;

    @Column(name = "initiated_by", nullable = false, length = 100)
    private String initiatedBy;

    @Builder.Default
    @Column(name = "is_automated")
    private Boolean isAutomated = true;
}
