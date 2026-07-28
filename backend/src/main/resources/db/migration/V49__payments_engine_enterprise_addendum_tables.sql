-- V49: Payments Engine Enterprise Addendum Tables (Gateway Routing, Tokenization, Subscriptions, Disputes, & Reconciliation)

-- 1. Payment Gateway Routing Table
CREATE TABLE IF NOT EXISTS payment_gateway_routing (
    id UUID PRIMARY KEY,
    gateway_name VARCHAR(40) NOT NULL UNIQUE,
    priority_order INT NOT NULL DEFAULT 1,
    success_rate_percent DOUBLE PRECISION NOT NULL DEFAULT 99.5,
    average_latency_ms INT NOT NULL DEFAULT 45,
    is_active BOOLEAN DEFAULT TRUE,
    is_blacklisted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Payment Tokens Table (PCI Compliant Token References)
CREATE TABLE IF NOT EXISTS payment_tokens (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    provider_name VARCHAR(40) NOT NULL,
    payment_token_ref VARCHAR(255) NOT NULL UNIQUE,
    card_alias VARCHAR(20) NOT NULL, -- e.g. "VISA ending 4242"
    expiry_month INT NOT NULL,
    expiry_year INT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ptok_cust ON payment_tokens(customer_id);

-- 3. Payment Subscriptions Table
CREATE TABLE IF NOT EXISTS payment_subscriptions (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    plan_name VARCHAR(100) NOT NULL,
    billing_amount NUMERIC(12,2) NOT NULL,
    billing_interval VARCHAR(20) NOT NULL DEFAULT 'MONTHLY', -- MONTHLY, YEARLY
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, CANCELLED
    next_billing_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_psub_cust ON payment_subscriptions(customer_id);

-- 4. Payment Disputes Table (Chargebacks)
CREATE TABLE IF NOT EXISTS payment_disputes (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL REFERENCES payments(id),
    dispute_amount NUMERIC(12,2) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, UNDER_REVIEW, WON, LOST
    provider_case_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Payment Reconciliation Table
CREATE TABLE IF NOT EXISTS payment_reconciliation (
    id UUID PRIMARY KEY,
    reconciliation_date TIMESTAMP WITH TIME ZONE NOT NULL,
    provider_name VARCHAR(40) NOT NULL,
    expected_amount NUMERIC(12,2) NOT NULL,
    actual_amount NUMERIC(12,2) NOT NULL,
    variance_amount NUMERIC(12,2) NOT NULL DEFAULT 0.0,
    status VARCHAR(30) NOT NULL DEFAULT 'RECONCILED', -- RECONCILED, VARIANCE_DETECTED, EXCEPTION
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
