package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "chaos_experiments")
@SQLDelete(sql = "UPDATE chaos_experiments SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChaosExperiment extends BaseEntity {

    @Column(name = "experiment_code", nullable = false, unique = true, length = 50)
    private String experimentCode;

    @Column(name = "target_system", nullable = false, length = 100)
    private String targetSystem;

    @Column(name = "fault_type", nullable = false, length = 50)
    private String faultType; // POD_KILL, NETWORK_LATENCY, CONNECTION_TIMEOUT, DISK_FILL

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED";

    @Builder.Default
    @Column(name = "resilience_passed")
    private Boolean resiliencePassed = true;
}
