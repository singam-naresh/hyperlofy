-- V38: Create Pickup & Drop Engine Tables

-- 1. Pickup & Drop Orders Table
CREATE TABLE IF NOT EXISTS pickup_drop_orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    sender_name VARCHAR(100) NOT NULL,
    sender_contact VARCHAR(30) NOT NULL,
    pickup_address TEXT NOT NULL,
    pickup_latitude DOUBLE PRECISION NOT NULL,
    pickup_longitude DOUBLE PRECISION NOT NULL,
    pickup_instructions TEXT,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_contact VARCHAR(30) NOT NULL,
    delivery_address TEXT NOT NULL,
    delivery_latitude DOUBLE PRECISION NOT NULL,
    delivery_longitude DOUBLE PRECISION NOT NULL,
    delivery_instructions TEXT,
    delivery_type VARCHAR(30) NOT NULL DEFAULT 'SAME_DAY', -- SAME_DAY, EXPRESS, SCHEDULED
    status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    assigned_driver_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_pd_cust ON pickup_drop_orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_pd_driver ON pickup_drop_orders(assigned_driver_id);
CREATE INDEX IF NOT EXISTS idx_pd_status ON pickup_drop_orders(status);

-- 2. Status History Table
CREATE TABLE IF NOT EXISTS pickup_drop_status_history (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    change_reason VARCHAR(255),
    changed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Parcels Table
CREATE TABLE IF NOT EXISTS pickup_drop_parcels (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    category VARCHAR(50) NOT NULL, -- DOCUMENTS, KEYS, PARCELS, ELECTRONICS, CLOTHING
    description TEXT,
    declared_value DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    weight_kg DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    is_fragile BOOLEAN DEFAULT FALSE,
    is_liquid BOOLEAN DEFAULT FALSE,
    is_perishable BOOLEAN DEFAULT FALSE,
    is_high_value BOOLEAN DEFAULT FALSE,
    insurance_requested BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. OTPs Table
CREATE TABLE IF NOT EXISTS pickup_drop_otps (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    otp_code VARCHAR(10) NOT NULL,
    otp_type VARCHAR(20) NOT NULL, -- PICKUP, DELIVERY
    is_verified BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Proofs Table
CREATE TABLE IF NOT EXISTS pickup_drop_proofs (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    proof_type VARCHAR(20) NOT NULL, -- PICKUP_PHOTO, DELIVERY_PHOTO, SIGNATURE
    image_url VARCHAR(255) NOT NULL,
    gps_latitude DOUBLE PRECISION,
    gps_longitude DOUBLE PRECISION,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Routes Table
CREATE TABLE IF NOT EXISTS pickup_drop_routes (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    estimated_distance_km DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    actual_distance_km DOUBLE PRECISION DEFAULT 0.0,
    estimated_duration_minutes INT NOT NULL DEFAULT 30,
    actual_duration_minutes INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Returns Table
CREATE TABLE IF NOT EXISTS pickup_drop_returns (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    return_reason TEXT NOT NULL,
    return_status VARCHAR(30) NOT NULL DEFAULT 'RETURN_IN_PROGRESS', -- RETURN_IN_PROGRESS, RETURN_COMPLETED
    initiated_by VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 8. Disputes Table
CREATE TABLE IF NOT EXISTS pickup_drop_disputes (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    dispute_type VARCHAR(50) NOT NULL, -- LOST_PARCEL, DAMAGED_PARCEL, WRONG_DELIVERY, LATE_DELIVERY
    description TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, UNDER_REVIEW, RESOLVED, CLOSED
    resolution_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 9. Events Table
CREATE TABLE IF NOT EXISTS pickup_drop_events (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES pickup_drop_orders(id),
    event_type VARCHAR(50) NOT NULL, -- PickupDropCreated, DriverAssigned, PickupCompleted, TransitStarted, DeliveryCompleted, DisputeRaised
    payload TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
