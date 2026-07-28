package com.hyperlofy.backend.analytics.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "analytics_reports")
@SQLDelete(sql = "UPDATE analytics_reports SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReport extends BaseEntity {

    @Column(name = "report_name", nullable = false, length = 150)
    private String reportName;

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType; // OPERATIONAL, FINANCIAL, MERCHANT, DRIVER, EXECUTIVE

    @Builder.Default
    @Column(name = "format", nullable = false, length = 20)
    private String format = "CSV";

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED";

    @Column(name = "download_url")
    private String downloadUrl;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
}
