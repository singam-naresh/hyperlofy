package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "platform_security_events")
@SQLDelete(sql = "UPDATE platform_security_events SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformSecurityEvent extends BaseEntity {

    @Column(name = "event_code", nullable = false, length = 100)
    private String eventCode;

    @Column(name = "source_component", nullable = false, length = 100)
    private String sourceComponent;

    @Builder.Default
    @Column(name = "severity", nullable = false, length = 30)
    private String severity = "HIGH";

    @Column(name = "description", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "enforcement_action", nullable = false, length = 50)
    private String enforcementAction = "BLOCKED";
}
