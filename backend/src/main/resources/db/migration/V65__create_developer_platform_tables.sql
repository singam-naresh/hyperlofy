-- V65: Create Developer Experience (DX), API Platform & Internal Developer Platform (IDP) Tables (API Gateway Routes, Consumer API Keys, Service Catalog, & Event Catalog)

-- 1. API Routes Table
CREATE TABLE IF NOT EXISTS api_routes (
    id UUID PRIMARY KEY,
    route_id VARCHAR(100) NOT NULL UNIQUE,
    service_name VARCHAR(100) NOT NULL,
    path_pattern VARCHAR(255) NOT NULL,
    target_uri VARCHAR(255) NOT NULL,
    rate_limit_per_min INT NOT NULL DEFAULT 1000,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. API Keys Table
CREATE TABLE IF NOT EXISTS api_keys (
    id UUID PRIMARY KEY,
    key_value VARCHAR(100) NOT NULL UNIQUE,
    consumer_name VARCHAR(150) NOT NULL,
    developer_email VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, REVOKED, EXPIRED
    quota_daily INT NOT NULL DEFAULT 50000,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_apikey_email ON api_keys(developer_email);

-- 3. Service Catalog Table
CREATE TABLE IF NOT EXISTS service_catalog (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT NOT NULL,
    owner_team VARCHAR(100) NOT NULL,
    repository_url VARCHAR(255) NOT NULL,
    tech_stack VARCHAR(100) NOT NULL DEFAULT 'Java 21 / Spring Boot 3',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Event Catalog Table
CREATE TABLE IF NOT EXISTS event_catalog (
    id UUID PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL UNIQUE,
    kafka_topic VARCHAR(150) NOT NULL,
    schema_version VARCHAR(30) NOT NULL DEFAULT 'v1.0.0',
    producing_service VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
