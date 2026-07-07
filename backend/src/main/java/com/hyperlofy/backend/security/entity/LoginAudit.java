package com.hyperlofy.backend.security.entity;

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

@Entity
@Table(name = "login_audits")
@SQLDelete(sql = "UPDATE login_audits SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAudit extends BaseEntity {

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "login_status", nullable = false, length = 30)
    private String loginStatus; // SUCCESS, FAILED_CREDENTIALS, BLOCKED_RATE_LIMIT, BLOCKED_SUSPICIOUS, LOCKED_OUT

    @Column(name = "ip_address", nullable = false, length = 45)
    private String ipAddress;

    @Column(name = "device_fingerprint", length = 255)
    private String deviceFingerprint;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;
}
