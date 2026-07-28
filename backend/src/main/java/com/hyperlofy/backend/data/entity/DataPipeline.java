package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "data_pipelines")
@SQLDelete(sql = "UPDATE data_pipelines SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataPipeline extends BaseEntity {

    @Column(name = "pipeline_code", nullable = false, unique = true, length = 100)
    private String pipelineCode;

    @Column(name = "pipeline_name", nullable = false, length = 150)
    private String pipelineName;

    @Column(name = "pipeline_type", nullable = false, length = 50)
    private String pipelineType; // STREAMING, BATCH, CDC

    @Column(name = "source_system", nullable = false, length = 100)
    private String sourceSystem;

    @Builder.Default
    @Column(name = "target_layer", nullable = false, length = 30)
    private String targetLayer = "BRONZE"; // BRONZE, SILVER, GOLD

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "ACTIVE";
}
