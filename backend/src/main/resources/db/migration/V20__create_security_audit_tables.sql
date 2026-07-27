-- V20: Create Security Audit & Idempotency Storage Tables

CREATE TABLE IF NOT EXISTS security_audit_logs (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL, -- AUTH_FAILURE, RATE_LIMIT_EXCEEDED, ADMIN_ACTION
    actor_email VARCHAR(150),
    ip_address VARCHAR(50),
    request_uri VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sec_audit_event ON security_audit_logs(event_type);

CREATE TABLE IF NOT EXISTS idempotency_records (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    request_hash VARCHAR(100),
    response_body TEXT,
    status_code INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_idempotency_key ON idempotency_records(idempotency_key);
