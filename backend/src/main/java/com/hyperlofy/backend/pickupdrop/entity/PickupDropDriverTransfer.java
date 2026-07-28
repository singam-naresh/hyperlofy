package com.hyperlofy.backend.pickupdrop.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "pickup_drop_driver_transfers")
@SQLDelete(sql = "UPDATE pickup_drop_driver_transfers SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDropDriverTransfer extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "from_driver_id", nullable = false)
    private UUID fromDriverId;

    @Column(name = "to_driver_id", nullable = false)
    private UUID toDriverId;

    @Column(name = "transfer_reason", nullable = false, length = 100)
    private String transferReason; // VEHICLE_BREAKDOWN, SHIFT_CHANGE, EMERGENCY

    @Column(name = "transfer_otp", nullable = false, length = 10)
    private String transferOtp;

    @Builder.Default
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
}
