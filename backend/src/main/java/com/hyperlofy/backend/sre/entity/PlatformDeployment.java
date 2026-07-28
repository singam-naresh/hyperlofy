package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "platform_deployments")
@SQLDelete(sql = "UPDATE platform_deployments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDeployment extends BaseEntity {

    @Column(name = "deployment_number", nullable = false, unique = true, length = 100)
    private String deploymentNumber;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "version", nullable = false, length = 50)
    private String version;

    @Builder.Default
    @Column(name = "strategy", nullable = false, length = 30)
    private String strategy = "CANARY"; // CANARY, BLUE_GREEN, ROLLING

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // IN_PROGRESS, COMPLETED, ROLLED_BACK

    @Column(name = "deployed_by", nullable = false, length = 100)
    private String deployedBy;
}
