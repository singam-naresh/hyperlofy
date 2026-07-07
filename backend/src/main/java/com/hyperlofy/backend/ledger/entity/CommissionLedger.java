package com.hyperlofy.backend.ledger.entity;

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

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "commission_ledgers")
@SQLDelete(sql = "UPDATE commission_ledgers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommissionLedger extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "agent_id", nullable = false)
    private UUID agentId;

    @Column(name = "total_order_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalOrderAmount;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate; // as percentage, e.g. 10.00%

    @Column(name = "commission_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal commissionAmount; // platform fee

    @Column(name = "agent_share", nullable = false, precision = 12, scale = 2)
    private BigDecimal agentShare; // amount credited to agent
}
