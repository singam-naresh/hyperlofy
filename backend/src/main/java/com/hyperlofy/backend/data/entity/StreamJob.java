package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "stream_jobs")
@SQLDelete(sql = "UPDATE stream_jobs SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamJob extends BaseEntity {

    @Column(name = "job_name", nullable = false, unique = true, length = 150)
    private String jobName;

    @Builder.Default
    @Column(name = "engine_type", nullable = false, length = 50)
    private String engineType = "KAFKA_STREAMS"; // KAFKA_STREAMS, FLINK, SPARK_STREAMING

    @Column(name = "input_topic", nullable = false, length = 150)
    private String inputTopic;

    @Column(name = "output_topic", nullable = false, length = 150)
    private String outputTopic;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "RUNNING";

    @Builder.Default
    @Column(name = "throughput_eps", nullable = false)
    private Integer throughputEps = 5000;
}
