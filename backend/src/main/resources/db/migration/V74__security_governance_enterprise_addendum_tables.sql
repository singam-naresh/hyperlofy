-- V74: Create Enterprise Security Platform Enterprise Addendum Tables (Identity Lifecycle Workflows, Privacy Consent & DSAR Requests, SOAR Security Playbooks, & UEBA Profiles)

-- 1. Identity Lifecycle Table (Joiner-Mover-Leaver Workflows)
CREATE TABLE IF NOT EXISTS identity_lifecycle (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    workflow_type VARCHAR(50) NOT NULL, -- JOINER, MOVER, LEAVER
    birthright_role VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- IN_PROGRESS, COMPLETED, FAILED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_il_user ON identity_lifecycle(user_id);

-- 2. Privacy Consents Table (GDPR / Privacy Compliance)
CREATE TABLE IF NOT EXISTS privacy_consents (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    consent_type VARCHAR(100) NOT NULL, -- MARKETING, ANALYTICS, THIRD_PARTY_SHARING
    granted BOOLEAN NOT NULL DEFAULT TRUE,
    ip_address VARCHAR(50),
    granted_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pc_user ON privacy_consents(user_id);

-- 3. Data Subject Requests Table (DSAR / Right to Access & Erasure)
CREATE TABLE IF NOT EXISTS data_subject_requests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    request_type VARCHAR(50) NOT NULL, -- RIGHT_TO_ACCESS, RIGHT_TO_ERASURE, RIGHT_TO_RECTIFICATION
    status VARCHAR(30) NOT NULL DEFAULT 'PROCESSED', -- SUBMITTED, IN_REVIEW, PROCESSED, REJECTED
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dsr_user ON data_subject_requests(user_id);

-- 4. Security Playbooks Table (SOAR Automated Incident Response)
CREATE TABLE IF NOT EXISTS security_playbooks (
    id UUID PRIMARY KEY,
    playbook_code VARCHAR(100) NOT NULL UNIQUE,
    playbook_name VARCHAR(150) NOT NULL,
    trigger_event VARCHAR(100) NOT NULL, -- BRUTE_FORCE_DETECTED, CREDENTIAL_LEAK, HIGH_RISK_GEO
    automated_action VARCHAR(100) NOT NULL, -- SUSPEND_USER, ISOLATE_POD, ROTATE_CERT
    execution_status VARCHAR(30) NOT NULL DEFAULT 'EXECUTED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
