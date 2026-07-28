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
@Table(name = "pitr_history")
@SQLDelete(sql = "UPDATE pitr_history SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PitrHistory extends BaseEntity {

    @Column(name = "target_time", nullable = false)
    private ZonedDateTime targetTime;

    @Column(name = "target_lsn", length = 100)
    private String targetLsn;

    @Builder.Default
    @Column(name = "timeline_id")
    private Integer timelineId = 1;

    @Builder.Default
    @Column(name = "recovery_status", nullable = false, length = 30)
    private String recoveryStatus = "SUCCESS";

    @Column(name = "executed_by", nullable = false, length = 100)
    private String executedBy;
}
