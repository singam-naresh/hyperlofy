-- V37: Buy For Me Enterprise Addendum Tables (Budget Controls, Substitutions, Sessions, Expenses, Fraud, & SLA)

-- 1. Budget History Table
CREATE TABLE IF NOT EXISTS buy_for_me_budget_history (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    original_budget DOUBLE PRECISION NOT NULL,
    requested_budget DOUBLE PRECISION NOT NULL,
    variance_percentage DOUBLE PRECISION NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED', -- REQUESTED, APPROVED, REJECTED
    reason TEXT,
    requested_by VARCHAR(50) NOT NULL, -- DRIVER, CUSTOMER, ADMIN
    approved_by VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_bfm_bh_order ON buy_for_me_budget_history(order_id);

-- 2. Substitutions Table
CREATE TABLE IF NOT EXISTS buy_for_me_substitutions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    original_item_name VARCHAR(150) NOT NULL,
    substitute_item_name VARCHAR(150) NOT NULL,
    substitute_brand VARCHAR(100),
    substitute_price DOUBLE PRECISION NOT NULL,
    suggested_by VARCHAR(50) NOT NULL, -- DRIVER, SYSTEM
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    rejection_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_bfm_sub_order ON buy_for_me_substitutions(order_id);

-- 3. Store Visits Table
CREATE TABLE IF NOT EXISTS buy_for_me_store_visits (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    store_name VARCHAR(150) NOT NULL,
    store_address TEXT,
    sequence_order INT NOT NULL DEFAULT 1,
    arrival_time TIMESTAMP WITH TIME ZONE,
    departure_time TIMESTAMP WITH TIME ZONE,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Shopping Sessions Table
CREATE TABLE IF NOT EXISTS buy_for_me_shopping_sessions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    driver_id UUID NOT NULL,
    session_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, PAUSED, COMPLETED
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP WITH TIME ZONE,
    items_completed INT DEFAULT 0,
    items_pending INT DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Receipt Versions Table
CREATE TABLE IF NOT EXISTS buy_for_me_receipt_versions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    version_number INT NOT NULL DEFAULT 1,
    receipt_image_url VARCHAR(255) NOT NULL,
    bill_amount DOUBLE PRECISION NOT NULL,
    invoice_number VARCHAR(100),
    modified_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Driver Expenses Table
CREATE TABLE IF NOT EXISTS buy_for_me_expenses (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    driver_id UUID NOT NULL,
    expense_type VARCHAR(50) NOT NULL, -- PERSONAL_SPEND, ADVANCE_REIMBURSEMENT, PARKING_FEE
    amount DOUBLE PRECISION NOT NULL,
    receipt_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED, APPROVED, REJECTED, REIMBURSED
    approved_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Fraud Events Table
CREATE TABLE IF NOT EXISTS buy_for_me_fraud_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    fraud_type VARCHAR(50) NOT NULL, -- DUPLICATE_RECEIPT, ABNORMAL_PRICE_VARIANCE, GPS_MISMATCH
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    details TEXT,
    flagged_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 8. Compliance Records Table
CREATE TABLE IF NOT EXISTS buy_for_me_compliance (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    compliance_type VARCHAR(50) NOT NULL, -- PRESCRIPTION_VERIFICATION, AGE_VERIFICATION
    document_url VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    verified_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 9. SLA Metrics Table
CREATE TABLE IF NOT EXISTS buy_for_me_sla_metrics (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES buy_for_me_orders(id),
    shopping_duration_minutes INT DEFAULT 0,
    approval_duration_minutes INT DEFAULT 0,
    delivery_duration_minutes INT DEFAULT 0,
    sla_violated BOOLEAN DEFAULT FALSE,
    violation_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
