package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "archive_jobs")
@SQLDelete(sql = "UPDATE archive_jobs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveJob extends BaseEntity {

    @Column(name = "job_name", nullable = false, length = 100)
    private String jobName;

    @Column(name = "dataset_name", nullable = false, length = 100)
    private String datasetName;

    @Builder.Default
    @Column(name = "archived_record_count")
    private Integer archivedRecordCount = 0;

    @Builder.Default
    @Column(name = "job_status", nullable = false, length = 30)
    private String jobStatus = "COMPLETED";

    @Column(name = "executed_by", nullable = false, length = 100)
    private String executedBy;
}
