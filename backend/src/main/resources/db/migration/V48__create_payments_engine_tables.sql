-- V48: Create Payments Engine Tables (Unified Payment Orchestration & Idempotent Gateways)

-- 1. Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    payment_method VARCHAR(30) NOT NULL, -- UPI, CREDIT_CARD, DEBIT_CARD, WALLET, COD
    provider_name VARCHAR(40) NOT NULL DEFAULT 'RAZORPAY', -- RAZORPAY, STRIPE, CASHFREE, PHONEPE
    provider_payment_id VARCHAR(100),
    amount NUMERIC(12,2) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'INR',
    status VARCHAR(30) NOT NULL DEFAULT 'PAYMENT_CREATED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pay_order ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_pay_customer ON payments(customer_id);
CREATE INDEX IF NOT EXISTS idx_pay_status ON payments(status);

-- 2. Payment Transactions Table
CREATE TABLE IF NOT EXISTS payment_transactions (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL REFERENCES payments(id),
    transaction_type VARCHAR(30) NOT NULL, -- AUTHORIZE, CAPTURE, REFUND, VOID
    amount NUMERIC(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    provider_transaction_id VARCHAR(100),
    error_code VARCHAR(50),
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ptx_payment ON payment_transactions(payment_id);

-- 3. Payment Refunds Table
CREATE TABLE IF NOT EXISTS payment_refunds (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL REFERENCES payments(id),
    refund_amount NUMERIC(12,2) NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'REFUND_PENDING', -- REFUND_PENDING, REFUNDED, FAILED
    provider_refund_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pref_payment ON payment_refunds(payment_id);

-- 4. Payment Webhooks Table
CREATE TABLE IF NOT EXISTS payment_webhooks (
    id UUID PRIMARY KEY,
    provider_name VARCHAR(40) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    signature VARCHAR(255),
    is_processed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Payment Idempotency Table
CREATE TABLE IF NOT EXISTS payment_idempotency (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE,
    payment_id UUID NOT NULL,
    request_hash VARCHAR(64) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
