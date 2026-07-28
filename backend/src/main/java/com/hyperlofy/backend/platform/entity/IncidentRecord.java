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
@Table(name = "incident_records")
@SQLDelete(sql = "UPDATE incident_records SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentRecord extends BaseEntity {

    @Column(name = "incident_code", nullable = false, unique = true, length = 50)
    private String incidentCode;

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 20)
    private String severity = "SEV1"; // SEV1, SEV2, SEV3, SEV4

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "root_cause", columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "resolution_details", columnDefinition = "TEXT")
    private String resolutionDetails;

    @Builder.Default
    @Column(name = "detected_at")
    private ZonedDateTime detectedAt = ZonedDateTime.now();

    @Column(name = "resolved_at")
    private ZonedDateTime resolvedAt;

    @Builder.Default
    @Column(name = "mttd_seconds")
    private Integer mttdSeconds = 0;

    @Builder.Default
    @Column(name = "mttr_seconds")
    private Integer mttrSeconds = 0;
}
