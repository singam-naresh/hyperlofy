-- V77: Create Enterprise Workflow Automation & Business Process Platform Tables
-- WorkflowDefinition, WorkflowInstance, WorkflowTask, WorkflowHistory

-- 1. Workflow Definitions Table (BPM Process Templates & Versions)
CREATE TABLE IF NOT EXISTS workflow_definitions (
    id UUID PRIMARY KEY,
    workflow_key VARCHAR(100) NOT NULL UNIQUE,
    workflow_name VARCHAR(150) NOT NULL,
    workflow_type VARCHAR(80) NOT NULL, -- MERCHANT_REGISTRATION, KYC_APPROVAL, REFUND_APPROVAL, FRAUD_INVESTIGATION, ORDER_EXCEPTION, etc.
    version INTEGER NOT NULL DEFAULT 1,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    timeout_hours INTEGER NOT NULL DEFAULT 72,
    retry_limit INTEGER NOT NULL DEFAULT 3,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wd_key ON workflow_definitions(workflow_key);
CREATE INDEX IF NOT EXISTS idx_wd_type ON workflow_definitions(workflow_type);

-- 2. Workflow Instances Table (Running Process Instances with State Machine)
CREATE TABLE IF NOT EXISTS workflow_instances (
    id UUID PRIMARY KEY,
    definition_id UUID NOT NULL REFERENCES workflow_definitions(id),
    instance_ref VARCHAR(100) NOT NULL UNIQUE, -- Business reference e.g. ORDER-123-REFUND
    tenant_id UUID,
    initiator_user_id UUID NOT NULL,
    current_state VARCHAR(50) NOT NULL DEFAULT 'CREATED', -- CREATED, PENDING, WAITING_APPROVAL, IN_PROGRESS, APPROVED, REJECTED, CANCELLED, TIMED_OUT, FAILED, COMPLETED, COMPENSATED
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL', -- LOW, NORMAL, HIGH, CRITICAL
    due_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    correlation_key VARCHAR(150),
    business_context JSONB,
    retry_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wi_ref ON workflow_instances(instance_ref);
CREATE INDEX IF NOT EXISTS idx_wi_state ON workflow_instances(current_state);
CREATE INDEX IF NOT EXISTS idx_wi_tenant ON workflow_instances(tenant_id);
CREATE INDEX IF NOT EXISTS idx_wi_initiator ON workflow_instances(initiator_user_id);

-- 3. Workflow Tasks Table (Human Approval & Automated Service Tasks)
CREATE TABLE IF NOT EXISTS workflow_tasks (
    id UUID PRIMARY KEY,
    instance_id UUID NOT NULL REFERENCES workflow_instances(id),
    task_name VARCHAR(150) NOT NULL,
    task_type VARCHAR(50) NOT NULL DEFAULT 'HUMAN_APPROVAL', -- HUMAN_APPROVAL, SERVICE_CALL, KAFKA_PUBLISH, TIMER, SAGA_STEP
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING', -- PENDING, CLAIMED, IN_PROGRESS, COMPLETED, DELEGATED, ESCALATED, TIMED_OUT, FAILED
    assignee_user_id UUID,
    candidate_group VARCHAR(100), -- e.g. FINANCE_TEAM, COMPLIANCE_TEAM
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    due_at TIMESTAMP WITH TIME ZONE,
    claimed_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    completion_reason TEXT,
    is_compensation BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wt_instance ON workflow_tasks(instance_id);
CREATE INDEX IF NOT EXISTS idx_wt_assignee ON workflow_tasks(assignee_user_id);
CREATE INDEX IF NOT EXISTS idx_wt_status ON workflow_tasks(status);
CREATE INDEX IF NOT EXISTS idx_wt_group ON workflow_tasks(candidate_group);

-- 4. Workflow History Table (Full Audit Trail of State Transitions)
CREATE TABLE IF NOT EXISTS workflow_history (
    id UUID PRIMARY KEY,
    instance_id UUID NOT NULL REFERENCES workflow_instances(id),
    task_id UUID REFERENCES workflow_tasks(id),
    action VARCHAR(80) NOT NULL, -- STARTED, STATE_CHANGED, TASK_CLAIMED, TASK_COMPLETED, APPROVED, REJECTED, ESCALATED, COMPENSATED, TIMED_OUT
    from_state VARCHAR(50),
    to_state VARCHAR(50),
    actor_user_id UUID,
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wh_instance ON workflow_history(instance_id);
