package com.hyperlofy.backend.platform.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.ZonedDateTime;

@Entity
@Table(name = "secret_rotation_history")
@SQLDelete(sql = "UPDATE secret_rotation_history SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecretRotationHistory extends BaseEntity {

    @Column(name = "secret_key", nullable = false, length = 100)
    private String secretKey;

    @Column(name = "version_id", nullable = false, length = 100)
    private String versionId;

    @Builder.Default
    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName = "ENVIRONMENT";

    @Column(name = "rotated_by", nullable = false, length = 100)
    private String rotatedBy;

    @Builder.Default
    @Column(name = "rotated_at")
    private ZonedDateTime rotatedAt = ZonedDateTime.now();
}
