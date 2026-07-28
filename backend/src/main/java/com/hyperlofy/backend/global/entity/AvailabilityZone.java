package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "availability_zones")
@SQLDelete(sql = "UPDATE availability_zones SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityZone extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false)
    private GlobalRegion region;

    @Column(name = "zone_code", nullable = false, unique = true, length = 50)
    private String zoneCode;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "HEALTHY"; // HEALTHY, DEGRADED, FAILED

    @Builder.Default
    @Column(name = "cluster_capacity_nodes", nullable = false)
    private Integer clusterCapacityNodes = 32;
}
