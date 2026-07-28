package com.hyperlofy.backend.security.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "identity_lifecycle")
@SQLDelete(sql = "UPDATE identity_lifecycle SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityLifecycle extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "workflow_type", nullable = false, length = 50)
    private String workflowType; // JOINER, MOVER, LEAVER

    @Column(name = "birthright_role", nullable = false, length = 100)
    private String birthrightRole;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "COMPLETED"; // IN_PROGRESS, COMPLETED, FAILED
}
