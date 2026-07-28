package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "admin_incidents")
@SQLDelete(sql = "UPDATE admin_incidents SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminIncident extends BaseEntity {

    @Column(name = "incident_number", nullable = false, unique = true, length = 100)
    private String incidentNumber;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "incident_type", nullable = false, length = 50)
    private String incidentType; // SYSTEM_OUTAGE, DRIVER_ACCIDENT, MERCHANT_FRAUD

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "SEV2"; // SEV1, SEV2, SEV3

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";

    @Column(name = "reported_by", nullable = false, length = 100)
    private String reportedBy;

    @Column(name = "resolution_notes")
    private String resolutionNotes;
}
