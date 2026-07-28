package com.hyperlofy.backend.buyforme.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "buy_for_me_orders")
@SQLDelete(sql = "UPDATE buy_for_me_orders SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyForMeOrder extends BaseEntity {

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category; // MEDICINES, GROCERIES, FOOD, ELECTRONICS, GIFTS, HARDWARE

    @Builder.Default
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "preferred_brand", length = 100)
    private String preferredBrand;

    @Builder.Default
    @Column(name = "alternative_brand_allowed")
    private Boolean alternativeBrandAllowed = true;

    @Column(name = "max_budget", nullable = false)
    private Double maxBudget;

    @Column(name = "purchase_notes", columnDefinition = "TEXT")
    private String purchaseNotes;

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_latitude", nullable = false)
    private Double deliveryLatitude;

    @Column(name = "delivery_longitude", nullable = false)
    private Double deliveryLongitude;

    @Column(name = "delivery_instructions", columnDefinition = "TEXT")
    private String deliveryInstructions;

    @Column(name = "preferred_delivery_time")
    private ZonedDateTime preferredDeliveryTime;

    @Builder.Default
    @Column(name = "priority", length = 20)
    private String priority = "NORMAL";

    @Builder.Default
    @Column(name = "is_emergency")
    private Boolean isEmergency = false;

    @Builder.Default
    @Column(name = "is_medical")
    private Boolean isMedical = false;

    @Builder.Default
    @Column(name = "is_age_restricted")
    private Boolean isAgeRestricted = false;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "REQUESTED";

    @Column(name = "assigned_driver_id")
    private UUID assignedDriverId;
}
