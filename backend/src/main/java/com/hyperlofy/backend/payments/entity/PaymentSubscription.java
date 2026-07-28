package com.hyperlofy.backend.payments.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_subscriptions")
@SQLDelete(sql = "UPDATE payment_subscriptions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSubscription extends BaseEntity {

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Column(name = "billing_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal billingAmount;

    @Builder.Default
    @Column(name = "billing_interval", nullable = false, length = 20)
    private String billingInterval = "MONTHLY";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE"; // ACTIVE, PAUSED, CANCELLED

    @Column(name = "next_billing_date", nullable = false)
    private ZonedDateTime nextBillingDate;
}
