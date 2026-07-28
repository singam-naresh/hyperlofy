package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "admin_session_audit")
@SQLDelete(sql = "UPDATE admin_session_audit SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminSessionAudit extends BaseEntity {

    @Column(name = "admin_user", nullable = false, length = 100)
    private String adminUser;

    @Column(name = "ip_address", nullable = false, length = 50)
    private String ipAddress;

    @Builder.Default
    @Column(name = "session_status", nullable = false, length = 30)
    private String sessionStatus = "ACTIVE";

    @Builder.Default
    @Column(name = "privilege_level", nullable = false, length = 50)
    private String privilegeLevel = "SUPER_ADMIN";
}
