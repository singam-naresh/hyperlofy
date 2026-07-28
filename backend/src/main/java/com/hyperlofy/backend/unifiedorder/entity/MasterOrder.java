package com.hyperlofy.backend.unifiedorder.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterOrder extends BaseEntity {

    @Column(name = "global_order_number", nullable = false, unique = true, length = 50)
    private String globalOrderNumber;

    @Column(name = "business_order_id", nullable = false)
    private UUID businessOrderId;

    @Column(name = "order_type", nullable = false, length = 40)
    private String orderType; // MARKETPLACE, BUY_FOR_ME, PICKUP_DROP, FOOD, PHARMACY

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "driver_id")
    private UUID driverId;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "CREATED";

    @Builder.Default
    @Column(name = "payment_status", length = 30)
    private String paymentStatus = "PENDING";

    @Builder.Default
    @Column(name = "pricing_status", length = 30)
    private String pricingStatus = "COMPLETED";

    @Builder.Default
    @Column(name = "tracking_status", length = 30)
    private String trackingStatus = "NOT_STARTED";

    @Builder.Default
    @Column(name = "priority", length = 20)
    private String priority = "NORMAL";

    @Builder.Default
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "source_service", nullable = false, length = 50)
    private String sourceService;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "completed_at")
    private ZonedDateTime completedAt;

    @Column(name = "cancelled_at")
    private ZonedDateTime cancelledAt;
}
