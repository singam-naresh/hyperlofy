package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "admin_actions")
@SQLDelete(sql = "UPDATE admin_actions SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAction extends BaseEntity {

    @Column(name = "admin_user", nullable = false, length = 100)
    private String adminUser;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // REASSIGN_ORDER, APPROVE_MERCHANT, SUSPEND_DRIVER, TOGGLE_FLAG

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "description")
    private String description;
}
