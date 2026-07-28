package com.hyperlofy.backend.workflow.entity;

import com.hyperlofy.backend.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "workflow_forms")
@SQLDelete(sql = "UPDATE workflow_forms SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowForm extends BaseEntity {

    @Column(name = "form_key", nullable = false, unique = true, length = 100)
    private String formKey;

    @Column(name = "form_name", nullable = false, length = 150)
    private String formName;

    /**
     * Form types: TASK_FORM, START_FORM, CASE_FORM, STANDALONE
     */
    @Builder.Default
    @Column(name = "form_type", nullable = false, length = 50)
    private String formType = "TASK_FORM";

    /**
     * JSON array of field descriptors:
     * [{"name":"refundAmount","type":"NUMBER","label":"Refund Amount","required":true,"validation":{"min":0}}]
     * Types: TEXT, NUMBER, DATE, BOOLEAN, DROPDOWN, MULTI_SELECT, FILE_UPLOAD, SIGNATURE
     */
    @Column(name = "form_schema", nullable = false, columnDefinition = "jsonb")
    private String formSchema;

    @Builder.Default
    @Column(name = "version", nullable = false)
    private Integer version = 1;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
