package com.hyperlofy.backend.matching.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "matching_surge_zones")
@SQLDelete(sql = "UPDATE matching_surge_zones SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingSurgeZone extends BaseEntity {

    @Column(name = "zone_name", nullable = false, unique = true, length = 100)
    private String zoneName;

    @Builder.Default
    @Column(name = "demand_level", nullable = false)
    private Double demandLevel = 1.0;

    @Builder.Default
    @Column(name = "supply_level", nullable = false)
    private Double supplyLevel = 1.0;

    @Builder.Default
    @Column(name = "surge_multiplier", nullable = false)
    private Double surgeMultiplier = 1.0;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
