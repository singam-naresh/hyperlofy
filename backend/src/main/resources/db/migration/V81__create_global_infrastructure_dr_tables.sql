-- V81: Create Global Platform, Multi-Region Infrastructure & Disaster Recovery Tables

-- 1. Global Regions Table (India, Singapore, UAE, Europe, North America, Australia)
CREATE TABLE IF NOT EXISTS global_regions (
    id UUID PRIMARY KEY,
    region_code VARCHAR(50) NOT NULL UNIQUE, -- ap-south-1 (India), ap-southeast-1 (Singapore), me-central-1 (UAE), eu-central-1 (Europe), us-east-1 (NA), ap-southeast-2 (Australia)
    region_name VARCHAR(100) NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    deployment_mode VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PASSIVE, DRAINED, MAINTENANCE
    primary_cloud_provider VARCHAR(50) NOT NULL DEFAULT 'AWS',
    is_primary_region BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_gr_code ON global_regions(region_code);

-- 2. Availability Zones Table (Multi-AZ Deployment per Region)
CREATE TABLE IF NOT EXISTS availability_zones (
    id UUID PRIMARY KEY,
    region_id UUID NOT NULL REFERENCES global_regions(id),
    zone_code VARCHAR(50) NOT NULL UNIQUE, -- ap-south-1a, ap-south-1b, ap-south-1c
    status VARCHAR(30) NOT NULL DEFAULT 'HEALTHY', -- HEALTHY, DEGRADED, FAILED
    cluster_capacity_nodes INTEGER NOT NULL DEFAULT 32,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_az_region ON availability_zones(region_id);

-- 3. Disaster Recovery Plans Table (RPO/RTO Management & Automated Failover Orchestration)
CREATE TABLE IF NOT EXISTS disaster_recovery_plans (
    id UUID PRIMARY KEY,
    plan_name VARCHAR(150) NOT NULL UNIQUE,
    primary_region_id UUID NOT NULL REFERENCES global_regions(id),
    target_dr_region_id UUID NOT NULL REFERENCES global_regions(id),
    target_rpo_seconds INTEGER NOT NULL DEFAULT 5, -- Recovery Point Objective
    target_rto_seconds INTEGER NOT NULL DEFAULT 60, -- Recovery Time Objective
    status VARCHAR(30) NOT NULL DEFAULT 'READY', -- READY, EXECUTING, RECOVERED, FAILED
    last_drill_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dr_primary ON disaster_recovery_plans(primary_region_id);

-- 4. Backup Executions Table (Snapshot, Full, Incremental, Cross-Region Backups)
CREATE TABLE IF NOT EXISTS backup_executions (
    id UUID PRIMARY KEY,
    backup_code VARCHAR(100) NOT NULL UNIQUE,
    region_code VARCHAR(50) NOT NULL,
    backup_type VARCHAR(30) NOT NULL DEFAULT 'FULL', -- FULL, INCREMENTAL, SNAPSHOT, WAL
    storage_size_bytes BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- COMPLETED, IN_PROGRESS, FAILED
    s3_snapshot_uri VARCHAR(500) NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_be_code ON backup_executions(backup_code);

-- 5. Traffic Routing Policies Table (Geo DNS, Latency-Based & Failover Traffic Management)
CREATE TABLE IF NOT EXISTS traffic_routing_policies (
    id UUID PRIMARY KEY,
    policy_name VARCHAR(150) NOT NULL UNIQUE,
    routing_type VARCHAR(50) NOT NULL DEFAULT 'GEO_LATENCY', -- GEO_LATENCY, WEIGHTED, FAILOVER, HEALTH_BASED
    target_region_code VARCHAR(50) NOT NULL,
    traffic_weight_percent INTEGER NOT NULL DEFAULT 100,
    health_status VARCHAR(30) NOT NULL DEFAULT 'HEALTHY', -- HEALTHY, DEGRADED, DRAINED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_trp_region ON traffic_routing_policies(target_region_code);
