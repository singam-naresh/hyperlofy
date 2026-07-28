package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "feature_store")
@SQLDelete(sql = "UPDATE feature_store SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureStore extends BaseEntity {

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // USER, MERCHANT, DRIVER, ORDER

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(name = "feature_name", nullable = false, length = 100)
    private String featureName;

    @Column(name = "feature_value", nullable = false, length = 255)
    private String featureValue;

    @Builder.Default
    @Column(name = "feature_version", nullable = false, length = 30)
    private String featureVersion = "v1";
}
