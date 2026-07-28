package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "api_routes")
@SQLDelete(sql = "UPDATE api_routes SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRoute extends BaseEntity {

    @Column(name = "route_id", nullable = false, unique = true, length = 100)
    private String routeId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "path_pattern", nullable = false, length = 255)
    private String pathPattern;

    @Column(name = "target_uri", nullable = false, length = 255)
    private String targetUri;

    @Builder.Default
    @Column(name = "rate_limit_per_min", nullable = false)
    private Integer rateLimitPerMin = 1000;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
