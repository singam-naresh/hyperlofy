-- V3__Enterprise_Hardening_Schema.sql
-- Missing Enterprise Tables Provisioning with optimal indexing for low-latency queries

-- 1. Create Wallets Table
CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    balance DECIMAL(15,2) DEFAULT 0.00 NOT NULL,
    version BIGINT DEFAULT 0 NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_wallets_user ON wallets(user_id);

-- 2. Create Wallet Transactions Table
CREATE TABLE wallet_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    wallet_id UUID NOT NULL REFERENCES wallets(id) ON DELETE CASCADE,
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    reference_id UUID,
    description TEXT,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_wallet_tx_wallet ON wallet_transactions(wallet_id);
CREATE INDEX idx_wallet_tx_reference ON wallet_transactions(reference_id);

-- 3. Create Payments Table
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    payment_status VARCHAR(30) DEFAULT 'PENDING' NOT NULL,
    payment_gateway VARCHAR(30) NOT NULL,
    transaction_id VARCHAR(100),
    gateway_order_id VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_tx ON payments(transaction_id);

-- 4. Create Payment Events Table (Webhooks tracking)
CREATE TABLE payment_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_id UUID,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT,
    processed BOOLEAN DEFAULT FALSE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

-- 5. Create Refunds Table
CREATE TABLE refunds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_id UUID NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    refund_status VARCHAR(40) DEFAULT 'PENDING_APPROVAL' NOT NULL,
    reason TEXT NOT NULL,
    approved_by UUID REFERENCES users(id) ON DELETE SET NULL,
    approval_note TEXT,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_refunds_payment ON refunds(payment_id);
CREATE INDEX idx_refunds_status ON refunds(refund_status);

-- 6. Create Notifications Table
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE NOT NULL,
    type VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);

-- 7. Create Chat Messages Table
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message_text TEXT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_chat_messages_order ON chat_messages(order_id);

-- 8. Create Agent Locations Table (Tracking histories)
CREATE TABLE agent_locations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    accuracy DOUBLE PRECISION,
    bearing DOUBLE PRECISION,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_agent_locations_agent_time ON agent_locations(agent_id, created_at DESC);

-- 9. Create Assignment Histories Table
CREATE TABLE assignment_histories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    agent_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    assignment_time TIMESTAMP WITH TIME ZONE NOT NULL,
    acceptance_time TIMESTAMP WITH TIME ZONE,
    rejection_time TIMESTAMP WITH TIME ZONE,
    rejection_reason VARCHAR(255),
    reassignment_reason VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_assignment_histories_order ON assignment_histories(order_id);

-- 10. Create Assignment Audits Table
CREATE TABLE assignment_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    description TEXT,
    triggered_by VARCHAR(100),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_assignment_audits_order ON assignment_audits(order_id);

-- 11. Create Admin Audit Logs Table
CREATE TABLE admin_audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    admin_id UUID NOT NULL,
    admin_email VARCHAR(100) NOT NULL,
    action_type VARCHAR(100) NOT NULL,
    action_summary TEXT NOT NULL,
    ip_address VARCHAR(45),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_admin_audit_logs_admin ON admin_audit_logs(admin_id);
CREATE INDEX idx_admin_audit_logs_action ON admin_audit_logs(action_type);

-- 12. Create Campaigns Table
CREATE TABLE campaigns (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    coupon_code VARCHAR(30) UNIQUE NOT NULL,
    reward_amount DECIMAL(10,2) NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_campaigns_coupon ON campaigns(coupon_code);

-- 13. Create Referrals Table
CREATE TABLE referrals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    referrer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    referred_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    reward_amount DECIMAL(10,2) NOT NULL,
    is_processed BOOLEAN DEFAULT FALSE NOT NULL,
    referral_code VARCHAR(30),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_referrals_referrer ON referrals(referrer_id);
CREATE INDEX idx_referrals_referred ON referrals(referred_id);

-- 14. Create Agent Payout Profiles Table
CREATE TABLE agent_payout_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL UNIQUE,
    bank_account_number VARCHAR(50) NOT NULL,
    bank_ifsc_code VARCHAR(20) NOT NULL,
    bank_holder_name VARCHAR(150) NOT NULL,
    cumulative_earnings DECIMAL(12,2) DEFAULT 0.00 NOT NULL,
    current_balance DECIMAL(12,2) DEFAULT 0.00 NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_payout_profiles_agent ON agent_payout_profiles(agent_id);

-- 15. Create Withdrawal Requests Table
CREATE TABLE withdrawal_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL REFERENCES agent_payout_profiles(id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING' NOT NULL,
    rejection_reason VARCHAR(255),
    payout_transaction_reference VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_withdrawals_agent ON withdrawal_requests(agent_id);
CREATE INDEX idx_withdrawals_status ON withdrawal_requests(status);
