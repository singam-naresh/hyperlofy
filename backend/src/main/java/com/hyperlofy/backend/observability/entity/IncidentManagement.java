package com.hyperlofy.backend.observability.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "incident_management")
@SQLDelete(sql = "UPDATE incident_management SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncidentManagement extends BaseEntity {

    @Column(name = "incident_code", nullable = false, unique = true, length = 100)
    private String incidentCode;

    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "SEV1"; // SEV1, SEV2, SEV3

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "OPEN"; // OPEN, INVESTIGATING, MITIGATED, CLOSED

    @Column(name = "commander_user_id")
    private UUID commanderUserId;

    @Column(name = "war_room_url", length = 255)
    private String warRoomUrl;

    @Builder.Default
    @Column(name = "declared_at")
    private OffsetDateTime declaredAt = OffsetDateTime.now();

    @Column(name = "resolved_at")
    private OffsetDateTime resolvedAt;
}
