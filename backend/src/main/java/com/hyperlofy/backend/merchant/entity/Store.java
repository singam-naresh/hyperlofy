package com.hyperlofy.backend.merchant.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "stores")
@SQLDelete(sql = "UPDATE stores SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store extends BaseEntity {

    @Column(name = "merchant_id", nullable = false)
    private UUID merchantId;

    @Column(name = "store_name", nullable = false, length = 150)
    private String storeName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    @Column(name = "banner_url", length = 255)
    private String bannerUrl;

    @Column(name = "business_category", nullable = false, length = 100)
    private String businessCategory;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Builder.Default
    @Column(name = "delivery_radius_km", nullable = false)
    private Double deliveryRadiusKm = 5.0;

    @Builder.Default
    @Column(name = "prep_time_minutes", nullable = false)
    private Integer prepTimeMinutes = 20;

    @Builder.Default
    @Column(name = "store_status", nullable = false, length = 30)
    private String storeStatus = "CLOSED"; // OPEN, CLOSED, TEMPORARY_CLOSURE

    @Builder.Default
    @Column(name = "is_accepting_orders")
    private Boolean isAcceptingOrders = true;
}
