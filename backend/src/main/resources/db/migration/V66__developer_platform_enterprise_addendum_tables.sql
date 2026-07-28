-- V66: Create Developer Platform Enterprise Addendum Tables (Consumer API Contracts, Partner Ecosystem, Webhooks, & IDP Service Scorecards)

-- 1. API Contracts Table
CREATE TABLE IF NOT EXISTS api_contracts (
    id UUID PRIMARY KEY,
    contract_name VARCHAR(150) NOT NULL UNIQUE,
    api_name VARCHAR(100) NOT NULL,
    consumer_service VARCHAR(100) NOT NULL,
    schema_version VARCHAR(30) NOT NULL DEFAULT 'v1.0.0',
    validation_status VARCHAR(30) NOT NULL DEFAULT 'VALIDATED', -- VALIDATED, COMPATIBILITY_FAILED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Partner Applications Table
CREATE TABLE IF NOT EXISTS partner_applications (
    id UUID PRIMARY KEY,
    partner_name VARCHAR(150) NOT NULL,
    app_name VARCHAR(150) NOT NULL UNIQUE,
    client_id VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- PENDING, ACTIVE, SUSPENDED
    contact_email VARCHAR(150) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Partner Webhooks Table
CREATE TABLE IF NOT EXISTS partner_webhooks (
    id UUID PRIMARY KEY,
    partner_app_id UUID REFERENCES partner_applications(id),
    target_url VARCHAR(255) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    secret_key VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pwh_app ON partner_webhooks(partner_app_id);

-- 4. Service Scorecards Table
CREATE TABLE IF NOT EXISTS service_scorecards (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL UNIQUE,
    overall_score NUMERIC(5,2) NOT NULL DEFAULT 98.50,
    grade VARCHAR(5) NOT NULL DEFAULT 'A+',
    security_score NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    observability_score NUMERIC(5,2) NOT NULL DEFAULT 96.00,
    documentation_score NUMERIC(5,2) NOT NULL DEFAULT 99.50,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
