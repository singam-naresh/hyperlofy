package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "training_datasets")
@SQLDelete(sql = "UPDATE training_datasets SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDataset extends BaseEntity {

    @Column(name = "dataset_name", nullable = false, unique = true, length = 150)
    private String datasetName;

    @Column(name = "model_type", nullable = false, length = 100)
    private String modelType; // DEMAND_FORECASTING, DYNAMIC_PRICING, FRAUD_DETECTION

    @Column(name = "storage_path", nullable = false, length = 255)
    private String storagePath;

    @Builder.Default
    @Column(name = "sample_count", nullable = false)
    private Integer sampleCount = 1000000;

    @Builder.Default
    @Column(name = "feature_version", nullable = false, length = 30)
    private String featureVersion = "v1.0";
}
