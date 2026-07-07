package com.hyperlofy.backend.analytics.entity;

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
import java.time.LocalDate;

@Entity
@Table(name = "analytics_snapshots")
@SQLDelete(sql = "UPDATE analytics_snapshots SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSnapshot extends BaseEntity {

    @Column(name = "snapshot_date", nullable = false, unique = true)
    private LocalDate snapshotDate;

    @Column(name = "total_orders", nullable = false)
    private int totalOrders;

    @Column(name = "total_revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalRevenue;

    @Column(name = "active_agents", nullable = false)
    private int activeAgents;

    @Column(name = "new_customers", nullable = false)
    private int newCustomers;

    @Column(name = "success_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal successRate;

    @Column(name = "escrow_balance", nullable = false, precision = 15, scale = 2)
    private BigDecimal escrowBalance;
}
