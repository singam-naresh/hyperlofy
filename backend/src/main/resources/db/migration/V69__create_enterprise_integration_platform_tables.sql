-- V69: Create Enterprise Integration Platform (EIP) & B2B Connectivity Tables (Connectors, Sync Jobs, Inbound Webhooks, & Failure/DLQ Logs)

-- 1. Integration Connectors Table
CREATE TABLE IF NOT EXISTS integration_connectors (
    id UUID PRIMARY KEY,
    connector_code VARCHAR(100) NOT NULL UNIQUE,
    connector_name VARCHAR(150) NOT NULL,
    system_type VARCHAR(50) NOT NULL, -- ERP, CRM, ACCOUNTING, SHIPPING
    provider_name VARCHAR(100) NOT NULL, -- SAP, SALESFORCE, TALLY, QUICKBOOKS, FEDEX
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, DEGRADED
    endpoint_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Integration Jobs Table
CREATE TABLE IF NOT EXISTS integration_jobs (
    id UUID PRIMARY KEY,
    connector_id UUID REFERENCES integration_connectors(id),
    job_type VARCHAR(100) NOT NULL, -- INVENTORY_SYNC, INVOICE_EXPORT, ORDER_SYNC
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- RUNNING, COMPLETED, FAILED
    records_processed INT NOT NULL DEFAULT 0,
    records_failed INT NOT NULL DEFAULT 0,
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ij_connector ON integration_jobs(connector_id);

-- 3. Integration Webhooks Table
CREATE TABLE IF NOT EXISTS integration_webhooks (
    id UUID PRIMARY KEY,
    connector_id UUID REFERENCES integration_connectors(id),
    event_type VARCHAR(100) NOT NULL,
    payload_hash VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PROCESSED',
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_iw_connector ON integration_webhooks(connector_id);

-- 4. Integration Failures Table (DLQ)
CREATE TABLE IF NOT EXISTS integration_failures (
    id UUID PRIMARY KEY,
    connector_id UUID REFERENCES integration_connectors(id),
    error_message TEXT NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'LOGGED', -- LOGGED, REPLAYED, DISCARDED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_if_connector ON integration_failures(connector_id);
