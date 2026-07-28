-- V30: Create Platform Foundation Tables (Part 1)

-- 1. Configuration Audit History Table
CREATE TABLE IF NOT EXISTS configuration_audit_logs (
    id UUID PRIMARY KEY,
    config_namespace VARCHAR(100) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    environment VARCHAR(30) DEFAULT 'PRODUCTION',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_cfg_audit_namespace ON configuration_audit_logs(config_namespace);

-- 2. API Gateway Dynamic Routes Table
CREATE TABLE IF NOT EXISTS gateway_dynamic_routes (
    id UUID PRIMARY KEY,
    route_id VARCHAR(100) NOT NULL UNIQUE,
    path_pattern VARCHAR(255) NOT NULL,
    target_uri VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    rate_limit_per_second INT DEFAULT 100,
    requires_auth BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
