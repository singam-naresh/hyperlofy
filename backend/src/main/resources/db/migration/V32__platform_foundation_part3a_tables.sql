-- V32: Platform Foundation Part 3A Tables (Disaster Recovery, HA, & Incident Management)

-- 1. DR Recovery Metrics Table
CREATE TABLE IF NOT EXISTS dr_recovery_metrics (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL UNIQUE,
    target_rto_seconds INT NOT NULL DEFAULT 300, -- 5 mins
    target_rpo_seconds INT NOT NULL DEFAULT 0,   -- near zero
    actual_rto_seconds INT DEFAULT 0,
    actual_rpo_seconds INT DEFAULT 0,
    last_dr_test_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. DR Failover Logs Table
CREATE TABLE IF NOT EXISTS dr_failover_logs (
    id UUID PRIMARY KEY,
    target_system VARCHAR(100) NOT NULL, -- DATABASE, REDIS, GATEWAY, PAYMENT_GATEWAY
    old_active_node VARCHAR(255) NOT NULL,
    new_active_node VARCHAR(255) NOT NULL,
    failover_reason VARCHAR(255) NOT NULL,
    initiated_by VARCHAR(100) NOT NULL,
    is_automated BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dr_failover_system ON dr_failover_logs(target_system);

-- 3. Incident Records Table
CREATE TABLE IF NOT EXISTS incident_records (
    id UUID PRIMARY KEY,
    incident_code VARCHAR(50) NOT NULL UNIQUE,
    severity VARCHAR(20) NOT NULL DEFAULT 'SEV1', -- SEV1, SEV2, SEV3, SEV4
    title VARCHAR(200) NOT NULL,
    root_cause TEXT,
    resolution_details TEXT,
    detected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    mttd_seconds INT DEFAULT 0,
    mttr_seconds INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. DR Operational Runbooks Table
CREATE TABLE IF NOT EXISTS dr_runbooks (
    id UUID PRIMARY KEY,
    runbook_code VARCHAR(50) NOT NULL UNIQUE,
    target_module VARCHAR(100) NOT NULL,
    title VARCHAR(200) NOT NULL,
    symptoms TEXT NOT NULL,
    diagnosis_steps TEXT NOT NULL,
    recovery_steps TEXT NOT NULL,
    rollback_steps TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
