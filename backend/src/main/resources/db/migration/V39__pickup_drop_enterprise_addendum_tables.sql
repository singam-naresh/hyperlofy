-- V39: Pickup & Drop Enterprise Addendum Tables (Chain of Custody, Transfers, Claims, Options, & Fraud)

-- 1. Chain of Custody History Table
CREATE TABLE IF NOT EXISTS pickup_drop_custody_history (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    custody_event VARCHAR(50) NOT NULL, -- CUSTODY_CREATED, PARCEL_RECEIVED, DRIVER_TRANSFER, DELIVERED, RETURN_CUSTODY
    handler_driver_id UUID NOT NULL,
    gps_latitude DOUBLE PRECISION,
    gps_longitude DOUBLE PRECISION,
    verification_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pd_ch_order ON pickup_drop_custody_history(order_id);

-- 2. Driver Transfers Table
CREATE TABLE IF NOT EXISTS pickup_drop_driver_transfers (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    from_driver_id UUID NOT NULL,
    to_driver_id UUID NOT NULL,
    transfer_reason VARCHAR(100) NOT NULL, -- VEHICLE_BREAKDOWN, SHIFT_CHANGE, EMERGENCY
    transfer_otp VARCHAR(10) NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Insurance Claims Table
CREATE TABLE IF NOT EXISTS pickup_drop_insurance_claims (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    claim_type VARCHAR(30) NOT NULL, -- DAMAGED_PARCEL, LOST_PARCEL
    claimed_amount DOUBLE PRECISION NOT NULL,
    description TEXT NOT NULL,
    evidence_url VARCHAR(255),
    status VARCHAR(30) NOT NULL DEFAULT 'SUBMITTED', -- SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, SETTLED
    reviewed_by VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Delivery Options Table
CREATE TABLE IF NOT EXISTS pickup_drop_delivery_options (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    option_type VARCHAR(50) NOT NULL, -- SAFE_PLACE, RECEPTION, SECURITY_GUARD, ALTERNATE_RECIPIENT
    instructions TEXT,
    recipient_relation VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Waiting Charges Table
CREATE TABLE IF NOT EXISTS pickup_drop_waiting_charges (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    waiting_minutes INT NOT NULL DEFAULT 0,
    waiting_fee DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    driver_compensation DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Fraud Events Table
CREATE TABLE IF NOT EXISTS pickup_drop_fraud_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    fraud_type VARCHAR(50) NOT NULL, -- OTP_ABUSE, GPS_SPOOFING, ROUTE_TAMPERING, PROOF_MANIPULATION
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. SLA Metrics Table
CREATE TABLE IF NOT EXISTS pickup_drop_sla_metrics (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    pickup_duration_minutes INT DEFAULT 0,
    transit_duration_minutes INT DEFAULT 0,
    total_delivery_minutes INT DEFAULT 0,
    sla_violated BOOLEAN DEFAULT FALSE,
    violation_reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
