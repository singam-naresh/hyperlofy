package com.hyperlofy.backend.eip.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "cdc_streams")
@SQLDelete(sql = "UPDATE cdc_streams SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdcStream extends BaseEntity {

    @Column(name = "stream_name", nullable = false, unique = true, length = 150)
    private String streamName;

    @Column(name = "source_table", nullable = false, length = 100)
    private String sourceTable;

    @Column(name = "kafka_topic", nullable = false, length = 150)
    private String kafkaTopic;

    @Builder.Default
    @Column(name = "status", nullable = false, length = 30)
    private String status = "STREAMING"; // PAUSED, STREAMING, ERROR

    @Builder.Default
    @Column(name = "lag_ms", nullable = false)
    private Long lagMs = 0L;
}
