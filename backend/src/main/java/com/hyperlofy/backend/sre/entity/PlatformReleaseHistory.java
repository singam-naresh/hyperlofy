package com.hyperlofy.backend.sre.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "platform_release_history")
@SQLDelete(sql = "UPDATE platform_release_history SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformReleaseHistory extends BaseEntity {

    @Column(name = "release_version", nullable = false, length = 50)
    private String releaseVersion;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Builder.Default
    @Column(name = "rollback_executed", nullable = false)
    private Boolean rollbackExecuted = false;

    @Builder.Default
    @Column(name = "verification_status", nullable = false, length = 30)
    private String verificationStatus = "PASSED";

    @Column(name = "approval_by", nullable = false, length = 100)
    private String approvalBy;
}
