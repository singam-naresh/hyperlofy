-- V78: Enterprise Workflow BPM Enterprise Addendum — BPMN Versioning, Case Management, DMN Rules Engine, Dynamic Forms, SLA Escalation & Process Analytics

-- 1. Workflow Versions Table (BPM Version Control: DRAFT → ACTIVE → ARCHIVED)
CREATE TABLE IF NOT EXISTS workflow_versions (
    id UUID PRIMARY KEY,
    definition_id UUID NOT NULL REFERENCES workflow_definitions(id),
    version_number INTEGER NOT NULL DEFAULT 1,
    version_status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', -- DRAFT, ACTIVE, ARCHIVED
    version_notes TEXT,
    published_by UUID,
    published_at TIMESTAMP WITH TIME ZONE,
    archived_at TIMESTAMP WITH TIME ZONE,
    bpmn_xml TEXT, -- Raw BPMN 2.0 XML descriptor for this version
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE (definition_id, version_number)
);

CREATE INDEX IF NOT EXISTS idx_wv_def ON workflow_versions(definition_id);
CREATE INDEX IF NOT EXISTS idx_wv_status ON workflow_versions(version_status);

-- 2. Workflow Cases Table (CMMN-inspired Adaptive Case Management)
CREATE TABLE IF NOT EXISTS workflow_cases (
    id UUID PRIMARY KEY,
    case_ref VARCHAR(100) NOT NULL UNIQUE,
    case_type VARCHAR(80) NOT NULL, -- FRAUD_INVESTIGATION, COMPLIANCE_INVESTIGATION, CUSTOMER_COMPLAINT, CHARGEBACK_REVIEW, LEGAL_REVIEW, MERCHANT_SUSPENSION, DELIVERY_PARTNER_APPEAL, RISK_ASSESSMENT
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, ESCALATED, SUSPENDED, CLOSED, ARCHIVED
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, CRITICAL
    subject_user_id UUID,
    assignee_user_id UUID,
    tenant_id UUID,
    related_workflow_instance_id UUID REFERENCES workflow_instances(id),
    due_at TIMESTAMP WITH TIME ZONE,
    closed_at TIMESTAMP WITH TIME ZONE,
    resolution TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wc_ref ON workflow_cases(case_ref);
CREATE INDEX IF NOT EXISTS idx_wc_type ON workflow_cases(case_type);
CREATE INDEX IF NOT EXISTS idx_wc_status ON workflow_cases(status);
CREATE INDEX IF NOT EXISTS idx_wc_tenant ON workflow_cases(tenant_id);

-- 3. Workflow Case Notes Table (Case Evidence, Investigation Notes, Attachments)
CREATE TABLE IF NOT EXISTS workflow_case_notes (
    id UUID PRIMARY KEY,
    case_id UUID NOT NULL REFERENCES workflow_cases(id),
    author_user_id UUID NOT NULL,
    note_type VARCHAR(30) NOT NULL DEFAULT 'NOTE', -- NOTE, EVIDENCE, ATTACHMENT, DECISION
    content TEXT NOT NULL,
    attachment_url VARCHAR(500),
    is_internal BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wcn_case ON workflow_case_notes(case_id);

-- 4. Business Rules Table (DMN-inspired Decision Rules Engine)
CREATE TABLE IF NOT EXISTS business_rules (
    id UUID PRIMARY KEY,
    rule_key VARCHAR(100) NOT NULL UNIQUE,
    rule_name VARCHAR(150) NOT NULL,
    rule_category VARCHAR(80) NOT NULL, -- REFUND_APPROVAL, MERCHANT_RATING, DELIVERY_ASSIGNMENT, FRAUD_SCORING, ESCALATION_TRIGGER
    condition_field VARCHAR(100) NOT NULL, -- e.g. refund_amount, merchant_rating
    condition_operator VARCHAR(30) NOT NULL, -- LT, GT, LTE, GTE, EQ, BETWEEN
    condition_value_min NUMERIC(16,2),
    condition_value_max NUMERIC(16,2),
    action_type VARCHAR(80) NOT NULL, -- AUTO_APPROVE, REQUIRE_REVIEW, ESCALATE, REJECT, ASSIGN_POOL
    action_value VARCHAR(255), -- e.g. FINANCE_TEAM, SENIOR_FINANCE, AUTO
    priority INTEGER NOT NULL DEFAULT 10,
    is_active BOOLEAN DEFAULT TRUE,
    effective_from TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    effective_to TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_br_key ON business_rules(rule_key);
CREATE INDEX IF NOT EXISTS idx_br_category ON business_rules(rule_category);

-- 5. Workflow Forms Table (Metadata-Driven Dynamic Form Engine)
CREATE TABLE IF NOT EXISTS workflow_forms (
    id UUID PRIMARY KEY,
    form_key VARCHAR(100) NOT NULL UNIQUE,
    form_name VARCHAR(150) NOT NULL,
    form_type VARCHAR(50) NOT NULL DEFAULT 'TASK_FORM', -- TASK_FORM, START_FORM, CASE_FORM, STANDALONE
    form_schema JSONB NOT NULL, -- JSON array of field descriptors {name, type, label, required, options, validation}
    version INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Workflow Escalation Policies Table (SLA & Auto-Escalation Engine)
CREATE TABLE IF NOT EXISTS workflow_escalation_policies (
    id UUID PRIMARY KEY,
    policy_name VARCHAR(150) NOT NULL UNIQUE,
    applies_to_workflow_type VARCHAR(80), -- NULL = applies to all types
    warning_hours INTEGER NOT NULL DEFAULT 24, -- Reminder notification before breach
    breach_hours INTEGER NOT NULL DEFAULT 48, -- Auto-escalate after this many hours
    escalation_level_1_group VARCHAR(100) NOT NULL, -- First escalation target group
    escalation_level_2_group VARCHAR(100), -- Second escalation target group
    escalation_level_3_group VARCHAR(100), -- Final escalation target group
    auto_cancel_hours INTEGER, -- Cancel instance if unresolved after this duration
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Workflow Analytics Table (Process Performance Metrics per Workflow Type)
CREATE TABLE IF NOT EXISTS workflow_analytics (
    id UUID PRIMARY KEY,
    workflow_type VARCHAR(80) NOT NULL,
    period_date DATE NOT NULL,
    total_instances INTEGER NOT NULL DEFAULT 0,
    completed_instances INTEGER NOT NULL DEFAULT 0,
    failed_instances INTEGER NOT NULL DEFAULT 0,
    compensated_instances INTEGER NOT NULL DEFAULT 0,
    avg_execution_hours NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    avg_human_approval_hours NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    automation_ratio NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    sla_compliance_rate NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE (workflow_type, period_date)
);
