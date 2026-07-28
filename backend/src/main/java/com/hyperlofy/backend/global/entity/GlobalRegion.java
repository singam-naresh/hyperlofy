package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "global_regions")
@SQLDelete(sql = "UPDATE global_regions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalRegion extends BaseEntity {

    @Column(name = "region_code", nullable = false, unique = true, length = 50)
    private String regionCode;

    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;

    @Column(name = "country_code", nullable = false, length = 10)
    private String countryCode;

    @Builder.Default
    @Column(name = "deployment_mode", nullable = false, length = 30)
    private String deploymentMode = "ACTIVE"; // ACTIVE, PASSIVE, DRAINED, MAINTENANCE

    @Builder.Default
    @Column(name = "primary_cloud_provider", nullable = false, length = 50)
    private String primaryCloudProvider = "AWS";

    @Builder.Default
    @Column(name = "is_primary_region", nullable = false)
    private Boolean isPrimaryRegion = false;
}
