package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "dr_runbooks")
@SQLDelete(sql = "UPDATE dr_runbooks SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrRunbook extends BaseEntity {

    @Column(name = "runbook_code", nullable = false, unique = true, length = 50)
    private String runbookCode;

    @Column(name = "target_module", nullable = false, length = 100)
    private String targetModule;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "symptoms", nullable = false, columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "diagnosis_steps", nullable = false, columnDefinition = "TEXT")
    private String diagnosisSteps;

    @Column(name = "recovery_steps", nullable = false, columnDefinition = "TEXT")
    private String recoverySteps;

    @Column(name = "rollback_steps", nullable = false, columnDefinition = "TEXT")
    private String rollbackSteps;
}
