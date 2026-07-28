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
@Table(name = "pickup_drop_orders")
@SQLDelete(sql = "UPDATE pickup_drop_orders SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDropOrder extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "sender_name", nullable = false, length = 100)
    private String senderName;

    @Column(name = "sender_contact", nullable = false, length = 30)
    private String senderContact;

    @Column(name = "pickup_address", nullable = false, columnDefinition = "TEXT")
    private String pickupAddress;

    @Column(name = "pickup_latitude", nullable = false)
    private Double pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false)
    private Double pickupLongitude;

    @Column(name = "pickup_instructions", columnDefinition = "TEXT")
    private String pickupInstructions;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "recipient_contact", nullable = false, length = 30)
    private String recipientContact;

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_latitude", nullable = false)
    private Double deliveryLatitude;

    @Column(name = "delivery_longitude", nullable = false)
    private Double deliveryLongitude;

    @Column(name = "delivery_instructions", columnDefinition = "TEXT")
    private String deliveryInstructions;

    @Builder.Default
    @Column(name = "delivery_type", nullable = false, length = 30)
    private String deliveryType = "SAME_DAY"; // SAME_DAY, EXPRESS, SCHEDULED

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REQUESTED";

    @Column(name = "assigned_driver_id")
    private UUID assignedDriverId;
}
