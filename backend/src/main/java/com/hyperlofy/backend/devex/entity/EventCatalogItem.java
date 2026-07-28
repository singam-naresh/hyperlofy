package com.hyperlofy.backend.devex.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "event_catalog")
@SQLDelete(sql = "UPDATE event_catalog SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventCatalogItem extends BaseEntity {

    @Column(name = "event_name", nullable = false, unique = true, length = 150)
    private String eventName;

    @Column(name = "kafka_topic", nullable = false, length = 150)
    private String kafkaTopic;

    @Builder.Default
    @Column(name = "schema_version", nullable = false, length = 30)
    private String schemaVersion = "v1.0.0";

    @Column(name = "producing_service", nullable = false, length = 100)
    private String producingService;
}
