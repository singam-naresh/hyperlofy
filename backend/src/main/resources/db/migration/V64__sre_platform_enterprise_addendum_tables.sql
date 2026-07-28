-- V64: Create SRE Platform Enterprise Addendum Tables (Advanced Resilience, Automated Rollbacks, Capacity Forecasting, & Infrastructure Runbooks)

-- 1. Platform Release History Table
CREATE TABLE IF NOT EXISTS platform_release_history (
    id UUID PRIMARY KEY,
    release_version VARCHAR(50) NOT NULL,
    service_name VARCHAR(100) NOT NULL,
    rollback_executed BOOLEAN DEFAULT FALSE,
    verification_status VARCHAR(30) NOT NULL DEFAULT 'PASSED',
    approval_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Platform Capacity Forecasts Table
CREATE TABLE IF NOT EXISTS platform_capacity_forecasts (
    id UUID PRIMARY KEY,
    cluster_name VARCHAR(100) NOT NULL,
    resource_type VARCHAR(50) NOT NULL, -- CPU, MEMORY, STORAGE, POD_COUNT
    current_utilization_pct NUMERIC(5,2) NOT NULL DEFAULT 45.00,
    forecasted_utilization_pct NUMERIC(5,2) NOT NULL DEFAULT 78.50,
    recommended_node_count INT NOT NULL DEFAULT 24,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Platform Security Events Table
CREATE TABLE IF NOT EXISTS platform_security_events (
    id UUID PRIMARY KEY,
    event_code VARCHAR(100) NOT NULL,
    source_component VARCHAR(100) NOT NULL,
    severity VARCHAR(30) NOT NULL DEFAULT 'HIGH',
    description TEXT NOT NULL,
    enforcement_action VARCHAR(50) NOT NULL DEFAULT 'BLOCKED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Platform Runbooks Table
CREATE TABLE IF NOT EXISTS platform_runbooks (
    id UUID PRIMARY KEY,
    runbook_name VARCHAR(150) NOT NULL UNIQUE,
    trigger_condition VARCHAR(150) NOT NULL,
    execution_mode VARCHAR(30) NOT NULL DEFAULT 'AUTOMATED', -- AUTOMATED, MANUAL
    success_rate_pct NUMERIC(5,2) NOT NULL DEFAULT 99.50,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
