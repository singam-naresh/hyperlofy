package com.hyperlofy.backend.governance.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "architecture_decision_records")
@SQLDelete(sql = "UPDATE architecture_decision_records SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureDecisionRecord extends BaseEntity {

    @Column(name = "adr_code", nullable = false, unique = true, length = 100)
    private String adrCode;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "APPROVED"; // PROPOSED, APPROVED, SUPERSEDED, RETIRED

    @Column(name = "author_user_id", nullable = false)
    private UUID authorUserId;

    @Column(name = "context", nullable = false, columnDefinition = "TEXT")
    private String context;

    @Column(name = "decision", nullable = false, columnDefinition = "TEXT")
    private String decision;

    @Column(name = "consequences", columnDefinition = "TEXT")
    private String consequences;

    @Column(name = "superseded_by_code", length = 100)
    private String supersededByCode;
}
