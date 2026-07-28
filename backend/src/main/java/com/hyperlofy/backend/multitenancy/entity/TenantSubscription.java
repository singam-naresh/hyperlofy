package com.hyperlofy.backend.multitenancy.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tenant_subscriptions")
@SQLDelete(sql = "UPDATE tenant_subscriptions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantSubscription extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;

    @Builder.Default
    @Column(name = "plan_name", nullable = false, length = 50)
    private String planName = "ENTERPRISE"; // BASIC, PROFESSIONAL, ENTERPRISE

    @Builder.Default
    @Column(name = "monthly_fee", nullable = false, precision = 16, scale = 2)
    private BigDecimal monthlyFee = new BigDecimal("9999.00");

    @Builder.Default
    @Column(name = "max_orders_per_month", nullable = false)
    private Integer maxOrdersPerMonth = 100000;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
