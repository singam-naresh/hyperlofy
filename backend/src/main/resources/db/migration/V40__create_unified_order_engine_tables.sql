-- V40: Create Unified Order Engine Tables (Single Source of Truth Master Orders)

-- 1. Master Orders Table
CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    global_order_number VARCHAR(50) NOT NULL UNIQUE,
    business_order_id UUID NOT NULL,
    order_type VARCHAR(40) NOT NULL, -- MARKETPLACE, BUY_FOR_ME, PICKUP_DROP, FOOD, PHARMACY
    customer_id UUID NOT NULL,
    merchant_id UUID,
    driver_id UUID,
    status VARCHAR(30) NOT NULL DEFAULT 'CREATED',
    payment_status VARCHAR(30) DEFAULT 'PENDING',
    pricing_status VARCHAR(30) DEFAULT 'COMPLETED',
    tracking_status VARCHAR(30) DEFAULT 'NOT_STARTED',
    priority VARCHAR(20) DEFAULT 'NORMAL', -- NORMAL, HIGH, URGENT, EMERGENCY
    total_amount DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    source_service VARCHAR(50) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    completed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_global ON orders(global_order_number);
CREATE INDEX IF NOT EXISTS idx_uo_bus ON orders(business_order_id);
CREATE INDEX IF NOT EXISTS idx_uo_cust ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_uo_merch ON orders(merchant_id);
CREATE INDEX IF NOT EXISTS idx_uo_driver ON orders(driver_id);
CREATE INDEX IF NOT EXISTS idx_uo_status ON orders(status);
CREATE INDEX IF NOT EXISTS idx_uo_type ON orders(order_type);

-- 2. Order Versions Table
CREATE TABLE IF NOT EXISTS order_versions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    version_number INT NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    reason VARCHAR(255),
    snapshot_json TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_ver_order ON order_versions(order_id);

-- 3. Order Timeline Table
CREATE TABLE IF NOT EXISTS order_timeline (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    event_name VARCHAR(100) NOT NULL,
    actor_id VARCHAR(100) NOT NULL,
    actor_type VARCHAR(30) NOT NULL, -- CUSTOMER, DRIVER, MERCHANT, SYSTEM, ADMIN
    source_service VARCHAR(50) NOT NULL,
    event_description TEXT,
    event_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_tl_order ON order_timeline(order_id);

-- 4. Order Cancellations Table
CREATE TABLE IF NOT EXISTS order_cancellations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    cancelled_by VARCHAR(50) NOT NULL,
    cancellation_reason TEXT NOT NULL,
    cancellation_fee DOUBLE PRECISION DEFAULT 0.0,
    refund_amount DOUBLE PRECISION DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Order Idempotency Keys Table
CREATE TABLE IF NOT EXISTS order_idempotency (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    order_id UUID NOT NULL REFERENCES orders(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_idem_key ON order_idempotency(idempotency_key);
