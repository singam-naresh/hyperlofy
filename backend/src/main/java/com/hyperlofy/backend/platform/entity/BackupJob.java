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
@Table(name = "backup_jobs")
@SQLDelete(sql = "UPDATE backup_jobs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BackupJob extends BaseEntity {

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Builder.Default
    @Column(name = "backup_type", nullable = false, length = 30)
    private String backupType = "FULL"; // FULL, INCREMENTAL, WAL, SNAPSHOT

    @Column(name = "target_system", nullable = false, length = 50)
    private String targetSystem; // POSTGRESQL, REDIS, CONFIGURATION

    @Builder.Default
    @Column(name = "schedule_cron", length = 50)
    private String scheduleCron = "0 0 * * *";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED";

    @Builder.Default
    @Column(name = "last_run_at")
    private ZonedDateTime lastRunAt = ZonedDateTime.now();

    @Column(name = "next_run_at")
    private ZonedDateTime nextRunAt;
}
