package com.hyperlofy.backend.zone.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "zones")
@SQLDelete(sql = "UPDATE zones SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Zone extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "center_latitude", nullable = false)
    private double centerLatitude;

    @Column(name = "center_longitude", nullable = false)
    private double centerLongitude;

    @Column(name = "radius_km", nullable = false)
    private double radiusKm;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
