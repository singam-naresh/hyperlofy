package com.hyperlofy.backend.observability.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "distributed_traces")
@SQLDelete(sql = "UPDATE distributed_traces SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributedTrace extends BaseEntity {

    @Column(name = "trace_id", nullable = false, length = 100)
    private String traceId;

    @Column(name = "span_id", nullable = false, length = 100)
    private String spanId;

    @Column(name = "parent_span_id", length = 100)
    private String parentSpanId;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Column(name = "operation_name", nullable = false, length = 150)
    private String operationName;

    @Builder.Default
    @Column(name = "duration_ms", nullable = false)
    private Long durationMs = 0L;

    @Builder.Default
    @Column(name = "status_code", nullable = false, length = 30)
    private String statusCode = "OK"; // OK, ERROR, UNSET
}
