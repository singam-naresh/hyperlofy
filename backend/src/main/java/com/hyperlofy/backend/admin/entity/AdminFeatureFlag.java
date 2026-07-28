package com.hyperlofy.backend.admin.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "admin_feature_flags")
@SQLDelete(sql = "UPDATE admin_feature_flags SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFeatureFlag extends BaseEntity {

    @Column(name = "flag_key", nullable = false, unique = true, length = 100)
    private String flagKey;

    @Column(name = "flag_name", nullable = false, length = 150)
    private String flagName;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean isEnabled = false;

    @Builder.Default
    @Column(name = "rollout_percentage", nullable = false)
    private Integer rolloutPercentage = 100;
}
