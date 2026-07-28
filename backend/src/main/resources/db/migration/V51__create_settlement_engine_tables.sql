-- V51: Create Settlement & Payout Engine Tables (Automated Merchant & Driver Bank Payouts)

-- 1. Settlements Table
CREATE TABLE IF NOT EXISTS settlements (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    payee_id UUID NOT NULL,
    payee_type VARCHAR(30) NOT NULL, -- MERCHANT, DRIVER, PLATFORM, TAX
    gross_amount NUMERIC(14,2) NOT NULL,
    platform_commission NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    tax_amount NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    net_amount NUMERIC(14,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SETTLEMENT_CREATED',
    scheduled_payout_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_settle_order ON settlements(order_id);
CREATE INDEX IF NOT EXISTS idx_settle_payee ON settlements(payee_id);
CREATE INDEX IF NOT EXISTS idx_settle_status ON settlements(status);

-- 2. Settlement Payouts Table
CREATE TABLE IF NOT EXISTS settlement_payouts (
    id UUID PRIMARY KEY,
    settlement_id UUID NOT NULL REFERENCES settlements(id),
    payout_reference VARCHAR(100) NOT NULL UNIQUE,
    bank_account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PROCESSING', -- PROCESSING, SETTLED, FAILED
    processed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_spayout_settle ON settlement_payouts(settlement_id);

-- 3. Beneficiary Accounts Table
CREATE TABLE IF NOT EXISTS beneficiary_accounts (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL UNIQUE,
    bank_name VARCHAR(100) NOT NULL,
    account_number VARCHAR(50) NOT NULL,
    ifsc_code VARCHAR(20) NOT NULL,
    account_holder_name VARCHAR(100) NOT NULL,
    is_verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Settlement Batches Table
CREATE TABLE IF NOT EXISTS settlement_batches (
    id UUID PRIMARY KEY,
    batch_reference VARCHAR(100) NOT NULL UNIQUE,
    total_settlements INT NOT NULL DEFAULT 0,
    total_batch_amount NUMERIC(16,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
