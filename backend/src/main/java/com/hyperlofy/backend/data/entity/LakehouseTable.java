package com.hyperlofy.backend.data.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "lakehouse_tables")
@SQLDelete(sql = "UPDATE lakehouse_tables SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LakehouseTable extends BaseEntity {

    @Column(name = "table_name", nullable = false, unique = true, length = 150)
    private String tableName;

    @Builder.Default
    @Column(name = "schema_namespace", nullable = false, length = 100)
    private String schemaNamespace = "hyperlofy_lakehouse";

    @Column(name = "lakehouse_layer", nullable = false, length = 30)
    private String lakehouseLayer; // BRONZE, SILVER, GOLD

    @Builder.Default
    @Column(name = "format", nullable = false, length = 30)
    private String format = "ICEBERG_PARQUET";

    @Builder.Default
    @Column(name = "total_records", nullable = false)
    private Long totalRecords = 0L;

    @Builder.Default
    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes = 0L;
}
