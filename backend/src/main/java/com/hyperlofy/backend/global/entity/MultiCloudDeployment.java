package com.hyperlofy.backend.global.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Table(name = "multi_cloud_deployments")
@SQLDelete(sql = "UPDATE multi_cloud_deployments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiCloudDeployment extends BaseEntity {

    @Column(name = "deployment_code", nullable = false, unique = true, length = 100)
    private String deploymentCode;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "cloud_provider", nullable = false, length = 50)
    private String cloudProvider; // AWS, AZURE, GCP

    @Column(name = "region_code", nullable = false, length = 50)
    private String regionCode;

    @Builder.Default
    @Column(name = "cluster_version", nullable = false, length = 50)
    private String clusterVersion = "v1.30.2";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "RUNNING"; // RUNNING, BURSTING, MIGRATING, DECOMMISSIONED

    @Builder.Default
    @Column(name = "monthly_cost_usd", nullable = false, precision = 16, scale = 2)
    private BigDecimal monthlyCostUsd = new BigDecimal("4500.00");
}
