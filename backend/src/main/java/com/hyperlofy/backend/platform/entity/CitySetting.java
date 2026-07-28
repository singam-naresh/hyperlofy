package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "city_settings")
@SQLDelete(sql = "UPDATE city_settings SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CitySetting extends BaseEntity {

    @Column(name = "city_name", nullable = false, unique = true, length = 100)
    private String cityName;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "max_delivery_radius_km")
    private Double maxDeliveryRadiusKm = 15.0;

    @Builder.Default
    @Column(name = "operating_hours", length = 100)
    private String operatingHours = "06:00-23:00";

    @Builder.Default
    @Column(name = "services_enabled", columnDefinition = "TEXT")
    private String servicesEnabled = "MARKETPLACE,BUY_FOR_ME,PICKUP_DROP";
}
