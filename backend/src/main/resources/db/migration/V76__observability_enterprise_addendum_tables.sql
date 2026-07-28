-- V76: Create Enterprise Observability Platform Enterprise Addendum Tables (Incident Management, Chaos Experiments, FinOps Service Costs, & Observability Dashboards)

-- 1. Incident Management Table (Major Incidents / War Room)
CREATE TABLE IF NOT EXISTS incident_management (
    id UUID PRIMARY KEY,
    incident_code VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(150) NOT NULL,
    severity VARCHAR(30) NOT NULL DEFAULT 'SEV1', -- SEV1, SEV2, SEV3
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, INVESTIGATING, MITIGATED, CLOSED
    commander_user_id UUID,
    war_room_url VARCHAR(255),
    declared_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Chaos Experiments Table (Chaos Engineering & Latency Injection)
CREATE TABLE IF NOT EXISTS chaos_experiments (
    id UUID PRIMARY KEY,
    experiment_name VARCHAR(150) NOT NULL UNIQUE,
    experiment_type VARCHAR(50) NOT NULL, -- POD_KILL, NETWORK_LATENCY, DB_FAILOVER
    target_service VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- SCHEDULED, RUNNING, COMPLETED, ABORTED
    resilience_score NUMERIC(5,2) NOT NULL DEFAULT 98.00,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Service Costs Table (FinOps Cloud Cost Attribution)
CREATE TABLE IF NOT EXISTS service_costs (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL UNIQUE,
    monthly_cost_usd NUMERIC(16,2) NOT NULL DEFAULT 1500.00,
    compute_cost NUMERIC(16,2) NOT NULL DEFAULT 900.00,
    storage_cost NUMERIC(16,2) NOT NULL DEFAULT 400.00,
    network_cost NUMERIC(16,2) NOT NULL DEFAULT 200.00,
    cost_status VARCHAR(30) NOT NULL DEFAULT 'OPTIMIZED', -- OPTIMIZED, OVER_BUDGET, ANOMALY
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Observability Dashboards Table (Executive SLI / SLO Dashboards)
CREATE TABLE IF NOT EXISTS observability_dashboards (
    id UUID PRIMARY KEY,
    dashboard_name VARCHAR(150) NOT NULL UNIQUE,
    category VARCHAR(50) NOT NULL DEFAULT 'EXECUTIVE_OPS', -- EXECUTIVE_OPS, FINOPS, SLO_COMPLIANCE
    grafana_url VARCHAR(255) NOT NULL,
    slo_percentage NUMERIC(5,2) NOT NULL DEFAULT 99.99,
    error_budget_remaining NUMERIC(5,2) NOT NULL DEFAULT 95.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
