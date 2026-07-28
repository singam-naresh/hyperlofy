-- V83: Create Enterprise Platform Governance, Architecture Compliance & Production Certification Tables

-- 1. Architecture Decision Records Table (ADR Registry — PROPOSED, APPROVED, SUPERSEDED, RETIRED)
CREATE TABLE IF NOT EXISTS architecture_decision_records (
    id UUID PRIMARY KEY,
    adr_code VARCHAR(100) NOT NULL UNIQUE, -- ADR-001, ADR-002
    title VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'APPROVED', -- PROPOSED, APPROVED, SUPERSEDED, RETIRED
    author_user_id UUID NOT NULL,
    context TEXT NOT NULL,
    decision TEXT NOT NULL,
    consequences TEXT,
    superseded_by_code VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_adr_code ON architecture_decision_records(adr_code);
CREATE INDEX IF NOT EXISTS idx_adr_status ON architecture_decision_records(status);

-- 2. Platform Standards Registry Table (SOLID, DDD, Hexagonal, API, Database Standards)
CREATE TABLE IF NOT EXISTS platform_standards (
    id UUID PRIMARY KEY,
    standard_key VARCHAR(100) NOT NULL UNIQUE,
    standard_name VARCHAR(150) NOT NULL,
    category VARCHAR(80) NOT NULL, -- ARCHITECTURE, API, DATABASE, SECURITY, CODING
    description TEXT NOT NULL,
    compliance_score NUMERIC(5,2) NOT NULL DEFAULT 100.00,
    is_mandatory BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ps_key ON platform_standards(standard_key);
CREATE INDEX IF NOT EXISTS idx_ps_cat ON platform_standards(category);

-- 3. Quality Gate Executions Table (Build, Test, Security Scan, API & DB Validation Gates)
CREATE TABLE IF NOT EXISTS quality_gate_executions (
    id UUID PRIMARY KEY,
    execution_code VARCHAR(100) NOT NULL UNIQUE,
    gate_name VARCHAR(100) NOT NULL, -- BUILD_GATE, TEST_GATE, SECURITY_SCAN, DEPENDENCY_SCAN, ARCHITECTURE_SCAN
    status VARCHAR(30) NOT NULL DEFAULT 'PASSED', -- PASSED, FAILED, WARNING
    total_checks INTEGER NOT NULL DEFAULT 10,
    passed_checks INTEGER NOT NULL DEFAULT 10,
    failed_checks INTEGER NOT NULL DEFAULT 0,
    execution_summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_qge_code ON quality_gate_executions(execution_code);

-- 4. Technical Debt Items Table (Technical Debt Backlog, Risk Score, & Resolution Tracking)
CREATE TABLE IF NOT EXISTS technical_debt_items (
    id UUID PRIMARY KEY,
    item_code VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(30) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, CRITICAL
    risk_score NUMERIC(5,2) NOT NULL DEFAULT 25.00,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED, WAIVED
    owner_user_id UUID NOT NULL,
    target_resolution_date DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tdi_code ON technical_debt_items(item_code);

-- 5. Production Certifications Table (Enterprise 10-Pillar Production Readiness Certificate)
CREATE TABLE IF NOT EXISTS production_certifications (
    id UUID PRIMARY KEY,
    certification_code VARCHAR(100) NOT NULL UNIQUE,
    platform_version VARCHAR(50) NOT NULL DEFAULT '1.0.0-SNAPSHOT',
    certified_by VARCHAR(150) NOT NULL DEFAULT 'Chief Enterprise Architect',
    architecture_certified BOOLEAN DEFAULT TRUE,
    security_certified BOOLEAN DEFAULT TRUE,
    performance_certified BOOLEAN DEFAULT TRUE,
    reliability_certified BOOLEAN DEFAULT TRUE,
    compliance_certified BOOLEAN DEFAULT TRUE,
    data_certified BOOLEAN DEFAULT TRUE,
    api_certified BOOLEAN DEFAULT TRUE,
    infrastructure_certified BOOLEAN DEFAULT TRUE,
    operations_certified BOOLEAN DEFAULT TRUE,
    overall_status VARCHAR(30) NOT NULL DEFAULT 'PRODUCTION_READY',
    certification_notes TEXT,
    certified_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pc_code ON production_certifications(certification_code);
