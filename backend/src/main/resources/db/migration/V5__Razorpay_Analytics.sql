-- V5__Razorpay_Analytics.sql
-- Schema additions to support payment audit logs, refund audit logs, and pre-aggregated daily analytics snapshots

CREATE TABLE payment_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    payment_id UUID,
    action_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE INDEX idx_payment_audits_pay ON payment_audits(payment_id);

CREATE TABLE refund_audits (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    refund_id UUID,
    action_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE INDEX idx_refund_audits_ref ON refund_audits(refund_id);

CREATE TABLE analytics_snapshots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    snapshot_date DATE NOT NULL UNIQUE,
    total_orders INTEGER NOT NULL,
    total_revenue DECIMAL(15,2) NOT NULL,
    active_agents INTEGER NOT NULL,
    new_customers INTEGER NOT NULL,
    success_rate DECIMAL(5,2) NOT NULL,
    escrow_balance DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL
);

CREATE INDEX idx_analytics_snapshots_date ON analytics_snapshots(snapshot_date);
