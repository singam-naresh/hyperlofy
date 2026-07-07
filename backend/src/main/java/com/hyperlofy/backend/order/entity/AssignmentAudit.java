package com.hyperlofy.backend.order.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "assignment_audits")
@SQLDelete(sql = "UPDATE assignment_audits SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentAudit extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "action_type", nullable = false, length = 50) // ASSIGNED, ACCEPTED, REJECTED, REASSIGNED
    private String actionType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "triggered_by", length = 100)
    private String triggeredBy;
}
