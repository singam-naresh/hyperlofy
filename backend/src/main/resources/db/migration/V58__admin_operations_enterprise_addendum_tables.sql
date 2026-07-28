-- V58: Create Admin Platform Enterprise Addendum Tables (Workflow Automation, Workforce Management, Session Tracking, & Governance)

-- 1. Admin Workflows Table
CREATE TABLE IF NOT EXISTS admin_workflows (
    id UUID PRIMARY KEY,
    workflow_name VARCHAR(100) NOT NULL UNIQUE,
    trigger_event VARCHAR(100) NOT NULL,
    current_step VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, COMPLETED
    sla_hours INT NOT NULL DEFAULT 24,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Admin Tasks Table
CREATE TABLE IF NOT EXISTS admin_tasks (
    id UUID PRIMARY KEY,
    task_number VARCHAR(100) NOT NULL UNIQUE,
    workflow_id UUID REFERENCES admin_workflows(id),
    title VARCHAR(255) NOT NULL,
    assigned_agent VARCHAR(100),
    priority VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, IN_PROGRESS, COMPLETED, ESCALATED
    due_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_atask_agent ON admin_tasks(assigned_agent);

-- 3. Admin Agent Workloads Table
CREATE TABLE IF NOT EXISTS admin_agent_workloads (
    id UUID PRIMARY KEY,
    agent_user VARCHAR(100) NOT NULL UNIQUE,
    active_cases_count INT NOT NULL DEFAULT 0,
    skill_category VARCHAR(50) NOT NULL DEFAULT 'GENERAL_SUPPORT',
    shift_status VARCHAR(30) NOT NULL DEFAULT 'ON_DUTY', -- ON_DUTY, OFF_DUTY, ON_BREAK
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Admin Session Audit Table
CREATE TABLE IF NOT EXISTS admin_session_audit (
    id UUID PRIMARY KEY,
    admin_user VARCHAR(100) NOT NULL,
    ip_address VARCHAR(50) NOT NULL,
    session_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    privilege_level VARCHAR(50) NOT NULL DEFAULT 'SUPER_ADMIN',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
