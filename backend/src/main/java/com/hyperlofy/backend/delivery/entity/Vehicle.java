package com.hyperlofy.backend.delivery.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "vehicles")
@SQLDelete(sql = "UPDATE vehicles SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle extends BaseEntity {

    @Column(name = "delivery_partner_id", nullable = false, unique = true)
    private UUID deliveryPartnerId;

    @Column(name = "vehicle_type", nullable = false, length = 50)
    private String vehicleType; // BIKE, SCOOTER, BICYCLE, CAR

    @Column(name = "vehicle_number", nullable = false, unique = true, length = 50)
    private String vehicleNumber;

    @Column(name = "rc_number", length = 50)
    private String rcNumber;

    @Column(name = "insurance_expiry_date", length = 50)
    private String insuranceExpiryDate;
}
