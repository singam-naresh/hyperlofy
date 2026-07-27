-- V24: Create AI Fraud Detection & Risk Intelligence Tables

-- 1. Risk Assessments Table
CREATE TABLE IF NOT EXISTS risk_assessments (
    id UUID PRIMARY KEY,
    target_id UUID NOT NULL, -- User ID, Order ID, Merchant ID, or Driver ID
    target_type VARCHAR(50) NOT NULL, -- ORDER, CUSTOMER, MERCHANT, DRIVER, REFUND
    risk_level VARCHAR(20) NOT NULL, -- LOW, MEDIUM, HIGH, CRITICAL
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    triggered_rules TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_risk_target ON risk_assessments(target_id);
CREATE INDEX IF NOT EXISTS idx_risk_level ON risk_assessments(risk_level);

-- 2. Fraud Rule Configurations Table
CREATE TABLE IF NOT EXISTS fraud_rule_configs (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    weight DOUBLE PRECISION DEFAULT 1.0,
    is_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
