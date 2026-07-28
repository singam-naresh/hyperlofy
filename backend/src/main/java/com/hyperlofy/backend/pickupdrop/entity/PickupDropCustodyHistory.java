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
@Table(name = "pickup_drop_custody_history")
@SQLDelete(sql = "UPDATE pickup_drop_custody_history SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDropCustodyHistory extends BaseEntity {

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "custody_event", nullable = false, length = 50)
    private String custodyEvent; // CUSTODY_CREATED, PARCEL_RECEIVED, DRIVER_TRANSFER, DELIVERED, RETURN_CUSTODY

    @Column(name = "handler_driver_id", nullable = false)
    private UUID handlerDriverId;

    @Column(name = "gps_latitude")
    private Double gpsLatitude;

    @Column(name = "gps_longitude")
    private Double gpsLongitude;

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;
}
