package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "platform_incidents")
@SQLDelete(sql = "UPDATE platform_incidents SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformIncident extends BaseEntity {

    @Column(name = "incident_number", nullable = false, unique = true, length = 100)
    private String incidentNumber;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "SEV2"; // SEV1, SEV2, SEV3

    @Column(name = "description", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "OPEN"; // OPEN, INVESTIGATING, MITIGATED, RESOLVED
}
