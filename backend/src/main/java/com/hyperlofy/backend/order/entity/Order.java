package com.hyperlofy.backend.order.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import com.hyperlofy.backend.user.entity.User;
import com.hyperlofy.backend.zone.entity.Zone;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
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
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private Zone zone;

    @Column(name = "merchant_id")
    private UUID merchantId;

    @Column(name = "store_name", nullable = false, length = 150)
    private String storeName;

    @Column(name = "store_latitude", nullable = false)
    private double storeLatitude;

    @Column(name = "store_longitude", nullable = false)
    private double storeLongitude;

    @Column(name = "delivery_address", nullable = false, columnDefinition = "TEXT")
    private String deliveryAddress;

    @Column(name = "delivery_latitude", nullable = false)
    private double deliveryLatitude;

    @Column(name = "delivery_longitude", nullable = false)
    private double deliveryLongitude;

    @Column(name = "distance_km", nullable = false)
    private double distanceKm;

    @Column(name = "delivery_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal deliveryFee;

    @Column(name = "items_desc", nullable = false, columnDefinition = "TEXT")
    private String itemsDesc;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 40)
    private OrderStatus orderStatus = OrderStatus.CREATED;

    @Column(name = "otp_code", length = 6)
    private String otpCode;
}
