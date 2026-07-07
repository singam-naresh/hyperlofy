-- V4__Schema_Expansion.sql
-- Enterprise-grade expansions for double-entry ledger, order normalization, and secure device session audits

-- 1. Create Order Items table (Normalized Commerce Engine)
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    item_name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    estimated_price DECIMAL(12,2) NOT NULL,
    final_price DECIMAL(12,2),
    item_status VARCHAR(50) DEFAULT 'AVAILABLE' NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_order_items_order ON order_items(order_id);

-- 2. Create Settlement Batches table
CREATE TABLE settlement_batches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    batch_status VARCHAR(30) DEFAULT 'OPEN' NOT NULL,
    total_settlement_amount DECIMAL(15,2) DEFAULT 0.00 NOT NULL,
    processed_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

-- 3. Create Escrow Transactions table
CREATE TABLE escrow_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    payment_id UUID NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(30) DEFAULT 'HELD' NOT NULL,
    released_at TIMESTAMP WITH TIME ZONE,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_escrow_order ON escrow_transactions(order_id);

-- 4. Create Double-Entry Ledger Entries table (Immutable ledger: Never Updated, Never Deleted)
CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    debit_account VARCHAR(100) NOT NULL,
    credit_account VARCHAR(100) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    reference_id UUID,
    order_id UUID,
    payment_id UUID,
    transaction_type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_ledger_debit ON ledger_entries(debit_account);
CREATE INDEX idx_ledger_credit ON ledger_entries(credit_account);
CREATE INDEX idx_ledger_order ON ledger_entries(order_id);
CREATE INDEX idx_ledger_tx_type ON ledger_entries(transaction_type);

-- 5. Create Commission Ledgers table
CREATE TABLE commission_ledgers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    agent_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    total_order_amount DECIMAL(12,2) NOT NULL,
    commission_rate DECIMAL(5,2) NOT NULL,
    commission_amount DECIMAL(12,2) NOT NULL,
    agent_share DECIMAL(12,2) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_commission_agent ON commission_ledgers(agent_id);
CREATE INDEX idx_commission_order ON commission_ledgers(order_id);

-- 6. Create Security Events audit logs table
CREATE TABLE security_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID,
    email VARCHAR(100) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    device_fingerprint VARCHAR(255),
    severity VARCHAR(20) NOT NULL,
    description TEXT,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_security_events_user ON security_events(user_id);
CREATE INDEX idx_security_events_email ON security_events(email);
CREATE INDEX idx_security_events_type ON security_events(event_type);

-- 7. Create Login Audits table (Brute-Force & Anomaly mitigation)
CREATE TABLE login_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(100) NOT NULL,
    login_status VARCHAR(30) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    device_fingerprint VARCHAR(255),
    failure_reason VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_login_audits_email_status ON login_audits(email, login_status);
CREATE INDEX idx_login_audits_ip ON login_audits(ip_address);

-- 8. Create Device Sessions table (Refresh token rotation auditing & fingerprinted sessions)
CREATE TABLE device_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    refresh_token VARCHAR(255) NOT NULL UNIQUE,
    device_fingerprint VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_device_sessions_user ON device_sessions(user_id);
CREATE INDEX idx_device_sessions_token ON device_sessions(refresh_token);
