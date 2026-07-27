package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Entity
@Table(name = "admin_audit_logs")
@SQLDelete(sql = "UPDATE admin_audit_logs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminAuditLog extends BaseEntity {

    @Column(name = "admin_id", nullable = false)
    private UUID adminId;

    @Column(name = "admin_email", nullable = false, length = 100)
    private String adminEmail;

    @Column(name = "action_type", nullable = false, length = 100)
    private String actionType;

    @Column(name = "action_summary", nullable = false, columnDefinition = "TEXT")
    private String actionSummary;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;
}
