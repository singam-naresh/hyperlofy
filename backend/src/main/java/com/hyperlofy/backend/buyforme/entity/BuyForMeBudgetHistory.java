package com.hyperlofy.backend.buyforme.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "buy_for_me_budget_history")
@SQLDelete(sql = "UPDATE buy_for_me_budget_history SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyForMeBudgetHistory extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "original_budget", nullable = false)
    private Double originalBudget;

    @Column(name = "requested_budget", nullable = false)
    private Double requestedBudget;

    @Column(name = "variance_percentage", nullable = false)
    private Double variancePercentage;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REQUESTED";

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "requested_by", nullable = false, length = 50)
    private String requestedBy;

    @Column(name = "approved_by", length = 50)
    private String approvedBy;
}
