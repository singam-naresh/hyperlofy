package com.hyperlofy.backend.unifiedorder.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "order_sagas")
@SQLDelete(sql = "UPDATE order_sagas SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSaga extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "saga_name", nullable = false, length = 100)
    private String sagaName;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "STARTED"; // STARTED, COMPLETED, COMPENSATING, FAILED

    @Builder.Default
    @Column(name = "current_step", nullable = false)
    private Integer currentStep = 1;
}
