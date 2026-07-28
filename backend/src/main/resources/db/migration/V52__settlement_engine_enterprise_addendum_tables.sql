-- V52: Create Settlement Engine Enterprise Addendum Tables (Treasury Operations, Payout Routing, Risk Detection & Governance)

-- 1. Settlement Treasury Table
CREATE TABLE IF NOT EXISTS settlement_treasury (
    id UUID PRIMARY KEY,
    reserve_pool_name VARCHAR(100) NOT NULL UNIQUE,
    available_liquidity NUMERIC(16,2) NOT NULL DEFAULT 50000000.00,
    locked_escrow NUMERIC(16,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Settlement Bank Routes Table
CREATE TABLE IF NOT EXISTS settlement_bank_routes (
    id UUID PRIMARY KEY,
    gateway_name VARCHAR(50) NOT NULL UNIQUE, -- RAZORPAY_X, CASHFREE, ICICI_API
    priority_order INT NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    success_rate NUMERIC(5,2) NOT NULL DEFAULT 99.50,
    avg_latency_ms INT NOT NULL DEFAULT 120,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Settlement Risk Events Table
CREATE TABLE IF NOT EXISTS settlement_risk_events (
    id UUID PRIMARY KEY,
    settlement_id UUID NOT NULL,
    risk_type VARCHAR(50) NOT NULL, -- DUPLICATE_PAYOUT, ABNORMAL_VELOCITY, HIGH_VALUE_TRANSFER
    risk_score NUMERIC(5,2) NOT NULL,
    action_taken VARCHAR(30) NOT NULL DEFAULT 'FLAGGED_FOR_REVIEW',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Settlement Governance Table
CREATE TABLE IF NOT EXISTS settlement_governance (
    id UUID PRIMARY KEY,
    settlement_id UUID NOT NULL,
    requested_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL', -- PENDING_APPROVAL, APPROVED, REJECTED
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
