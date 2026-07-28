package com.hyperlofy.backend.settlement.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "settlement_treasury")
@SQLDelete(sql = "UPDATE settlement_treasury SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementTreasury extends BaseEntity {

    @Column(name = "reserve_pool_name", nullable = false, unique = true, length = 100)
    private String reservePoolName;

    @Builder.Default
    @Column(name = "available_liquidity", nullable = false, precision = 16, scale = 2)
    private BigDecimal availableLiquidity = new BigDecimal("50000000.00");

    @Builder.Default
    @Column(name = "locked_escrow", nullable = false, precision = 16, scale = 2)
    private BigDecimal lockedEscrow = BigDecimal.ZERO;
}
