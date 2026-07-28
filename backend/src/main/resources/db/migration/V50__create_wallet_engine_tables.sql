-- V50: Create Wallet Engine & Enterprise Addendum Tables (Double-Entry Ledger, Treasury, & Spending Policies)

-- 1. Wallets Table
CREATE TABLE IF NOT EXISTS wallets (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL UNIQUE,
    owner_type VARCHAR(30) NOT NULL, -- CUSTOMER, DRIVER, MERCHANT, TREASURY
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    spendable_balance NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    reserved_balance NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    promotional_balance NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, FROZEN, CLOSED
    kyc_status VARCHAR(30) NOT NULL DEFAULT 'VERIFIED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wal_owner ON wallets(owner_id);
CREATE INDEX IF NOT EXISTS idx_wal_status ON wallets(status);

-- 2. Wallet Ledger Entries Table (Immutable Double-Entry Ledger)
CREATE TABLE IF NOT EXISTS wallet_ledger_entries (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    entry_type VARCHAR(30) NOT NULL, -- CREDIT, DEBIT, HOLD_LOCK, HOLD_RELEASE
    amount NUMERIC(14,2) NOT NULL,
    balance_after NUMERIC(14,2) NOT NULL,
    reference_id UUID,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_wled_wallet ON wallet_ledger_entries(wallet_id);

-- 3. Wallet Holds Table (Escrow / Reserve Locking)
CREATE TABLE IF NOT EXISTS wallet_holds (
    id UUID PRIMARY KEY,
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    order_id UUID NOT NULL,
    amount NUMERIC(14,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'LOCKED', -- LOCKED, RELEASED, CAPTURED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_whold_wallet ON wallet_holds(wallet_id);
CREATE INDEX IF NOT EXISTS idx_whold_order ON wallet_holds(order_id);

-- 4. Wallet Treasury Table
CREATE TABLE IF NOT EXISTS wallet_treasury (
    id UUID PRIMARY KEY,
    treasury_name VARCHAR(100) NOT NULL UNIQUE,
    total_reserve_amount NUMERIC(16,2) NOT NULL DEFAULT 10000000.00,
    escrow_amount NUMERIC(16,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Wallet Spending Rules Table
CREATE TABLE IF NOT EXISTS wallet_spending_rules (
    id UUID PRIMARY KEY,
    rule_name VARCHAR(100) NOT NULL UNIQUE,
    daily_spend_limit NUMERIC(14,2) NOT NULL DEFAULT 50000.00,
    max_transaction_limit NUMERIC(14,2) NOT NULL DEFAULT 10000.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Wallet Governance Table
CREATE TABLE IF NOT EXISTS wallet_governance (
    id UUID PRIMARY KEY,
    adjustment_type VARCHAR(30) NOT NULL, -- ADMIN_CREDIT, ADMIN_DEBIT, TREASURY_TRANSFER
    wallet_id UUID NOT NULL REFERENCES wallets(id),
    amount NUMERIC(14,2) NOT NULL,
    requested_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_APPROVAL',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
