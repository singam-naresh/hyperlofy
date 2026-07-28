package com.hyperlofy.backend.payments.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "payment_gateway_routing")
@SQLDelete(sql = "UPDATE payment_gateway_routing SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayRouting extends BaseEntity {

    @Column(name = "gateway_name", nullable = false, unique = true, length = 40)
    private String gatewayName;

    @Builder.Default
    @Column(name = "priority_order", nullable = false)
    private Integer priorityOrder = 1;

    @Builder.Default
    @Column(name = "success_rate_percent", nullable = false)
    private Double successRatePercent = 99.5;

    @Builder.Default
    @Column(name = "average_latency_ms", nullable = false)
    private Integer averageLatencyMs = 45;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_blacklisted")
    private Boolean isBlacklisted = false;
}
