package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "security_playbooks")
@SQLDelete(sql = "UPDATE security_playbooks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityPlaybook extends BaseEntity {

    @Column(name = "playbook_code", nullable = false, unique = true, length = 100)
    private String playbookCode;

    @Column(name = "playbook_name", nullable = false, length = 150)
    private String playbookName;

    @Column(name = "trigger_event", nullable = false, length = 100)
    private String triggerEvent; // BRUTE_FORCE_DETECTED, CREDENTIAL_LEAK, HIGH_RISK_GEO

    @Column(name = "automated_action", nullable = false, length = 100)
    private String automatedAction; // SUSPEND_USER, ISOLATE_POD, ROTATE_CERT

    @Builder.Default
    @Column(name = "execution_status", nullable = false, length = 30)
    private String executionStatus = "EXECUTED";
}
