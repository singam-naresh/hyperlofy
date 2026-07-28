-- V82: Create Global Platform Enterprise Addendum Tables (Autonomous Self-Healing, Multi-Cloud, Traffic Optimization, Certificates, & Executive Dashboards)

-- 1. Autonomous Recovery Executions Table (Self-Healing Service Restarts, Node Replacement, Secret Rotation)
CREATE TABLE IF NOT EXISTS autonomous_recovery_executions (
    id UUID PRIMARY KEY,
    execution_code VARCHAR(100) NOT NULL UNIQUE,
    target_service VARCHAR(100) NOT NULL,
    region_code VARCHAR(50) NOT NULL,
    action_type VARCHAR(80) NOT NULL, -- RESTART_POD, REPLACE_NODE, REDIS_FAILOVER, CERT_RENEW, SECRET_ROTATE
    trigger_reason TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- IN_PROGRESS, COMPLETED, FAILED
    execution_duration_ms BIGINT NOT NULL DEFAULT 1200,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_are_code ON autonomous_recovery_executions(execution_code);
CREATE INDEX IF NOT EXISTS idx_are_service ON autonomous_recovery_executions(target_service);

-- 2. Global Traffic Optimizations Table (AI-Powered Cost, Latency, & Health Traffic Shifting)
CREATE TABLE IF NOT EXISTS global_traffic_optimizations (
    id UUID PRIMARY KEY,
    optimization_code VARCHAR(100) NOT NULL UNIQUE,
    source_region_code VARCHAR(50) NOT NULL,
    target_region_code VARCHAR(50) NOT NULL,
    shifted_traffic_percent INTEGER NOT NULL DEFAULT 20,
    optimization_reason VARCHAR(150) NOT NULL, -- LATENCY_SPIKE, COST_SAVING, CARBON_REDUCTION, HEALTH_DEGRADATION
    latency_reduction_ms INTEGER NOT NULL DEFAULT 45,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, REVERTED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_gto_code ON global_traffic_optimizations(optimization_code);

-- 3. Multi-Cloud Deployments Table (Cross-Cloud AWS / Azure / GCP Governance)
CREATE TABLE IF NOT EXISTS multi_cloud_deployments (
    id UUID PRIMARY KEY,
    deployment_code VARCHAR(100) NOT NULL UNIQUE,
    service_name VARCHAR(100) NOT NULL,
    cloud_provider VARCHAR(50) NOT NULL, -- AWS, AZURE, GCP
    region_code VARCHAR(50) NOT NULL,
    cluster_version VARCHAR(50) NOT NULL DEFAULT 'v1.30.2',
    status VARCHAR(30) NOT NULL DEFAULT 'RUNNING', -- RUNNING, BURSTING, MIGRATING, DECOMMISSIONED
    monthly_cost_usd NUMERIC(16,2) NOT NULL DEFAULT 4500.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_mcd_code ON multi_cloud_deployments(deployment_code);

-- 4. Global Certificates Table (Automated Certificate Lifecycle & Renewal)
CREATE TABLE IF NOT EXISTS global_certificates (
    id UUID PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL UNIQUE,
    certificate_authority VARCHAR(100) NOT NULL DEFAULT 'LetsEncrypt',
    status VARCHAR(30) NOT NULL DEFAULT 'VALID', -- VALID, EXPIRING_SOON, RENEWED, REVOKED
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    auto_renew BOOLEAN DEFAULT TRUE,
    dns_provider VARCHAR(50) NOT NULL DEFAULT 'Route53',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_gc_domain ON global_certificates(domain_name);

-- 5. Executive Operations Dashboards Table (Global Availability, RPO/RTO Compliance, & Carbon Metrics)
CREATE TABLE IF NOT EXISTS executive_operations_dashboards (
    id UUID PRIMARY KEY,
    dashboard_key VARCHAR(100) NOT NULL UNIQUE,
    global_availability_percent NUMERIC(5,2) NOT NULL DEFAULT 99.99,
    rpo_compliance_percent NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    rto_compliance_percent NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    resilience_score NUMERIC(5,2) NOT NULL DEFAULT 98.50,
    carbon_emissions_kg NUMERIC(10,2) NOT NULL DEFAULT 1250.00,
    finops_savings_usd NUMERIC(16,2) NOT NULL DEFAULT 18500.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_eod_key ON executive_operations_dashboards(dashboard_key);
