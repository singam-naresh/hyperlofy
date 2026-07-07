package com.hyperlofy.backend.payment.entity;

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
@Table(name = "refund_audits")
@SQLDelete(sql = "UPDATE refund_audits SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAudit extends BaseEntity {

    @Column(name = "refund_id")
    private UUID refundId;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
