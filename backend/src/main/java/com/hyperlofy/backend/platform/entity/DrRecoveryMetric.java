package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "dr_recovery_metrics")
@SQLDelete(sql = "UPDATE dr_recovery_metrics SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrRecoveryMetric extends BaseEntity {

    @Column(name = "service_name", nullable = false, unique = true, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "target_rto_seconds", nullable = false)
    private Integer targetRtoSeconds = 300; // 5 mins

    @Builder.Default
    @Column(name = "target_rpo_seconds", nullable = false)
    private Integer targetRpoSeconds = 0;

    @Builder.Default
    @Column(name = "actual_rto_seconds")
    private Integer actualRtoSeconds = 0;

    @Builder.Default
    @Column(name = "actual_rpo_seconds")
    private Integer actualRpoSeconds = 0;

    @Builder.Default
    @Column(name = "last_dr_test_at")
    private ZonedDateTime lastDrTestAt = ZonedDateTime.now();
}
