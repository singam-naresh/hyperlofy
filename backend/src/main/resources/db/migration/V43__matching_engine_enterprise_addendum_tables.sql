-- V43: Matching Engine Enterprise Addendum Tables (Scheduled Dispatch, Surge Zones, Geofences, & Fairness)

-- 1. Matching Reservations Table
CREATE TABLE IF NOT EXISTS matching_reservations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    reserved_driver_id UUID NOT NULL,
    scheduled_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'RESERVED', -- RESERVED, CONFIRMED, EXPIRED, CANCELLED
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_mres_order ON matching_reservations(order_id);
CREATE INDEX IF NOT EXISTS idx_mres_driver ON matching_reservations(reserved_driver_id);

-- 2. Matching Surge Zones Table
CREATE TABLE IF NOT EXISTS matching_surge_zones (
    id UUID PRIMARY KEY,
    zone_name VARCHAR(100) NOT NULL UNIQUE,
    demand_level DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    supply_level DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    surge_multiplier DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Matching Geofences Table
CREATE TABLE IF NOT EXISTS matching_geofences (
    id UUID PRIMARY KEY,
    geofence_name VARCHAR(100) NOT NULL UNIQUE,
    center_latitude DOUBLE PRECISION NOT NULL,
    center_longitude DOUBLE PRECISION NOT NULL,
    radius_km DOUBLE PRECISION NOT NULL DEFAULT 5.0,
    max_active_drivers INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Matching Fairness Table
CREATE TABLE IF NOT EXISTS matching_fairness (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL UNIQUE,
    total_assignments INT NOT NULL DEFAULT 0,
    total_working_hours DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    acceptance_rate DOUBLE PRECISION NOT NULL DEFAULT 100.0,
    idle_time_minutes INT NOT NULL DEFAULT 0,
    fairness_score DOUBLE PRECISION NOT NULL DEFAULT 95.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Matching Fraud Events Table
CREATE TABLE IF NOT EXISTS matching_fraud_events (
    id UUID PRIMARY KEY,
    matching_request_id UUID NOT NULL,
    driver_id UUID NOT NULL,
    fraud_type VARCHAR(50) NOT NULL, -- GPS_SPOOFING, LOCATION_TELEPORTING, REPEATED_REJECT_PATTERN
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 6. Matching Dispatch Policies Table
CREATE TABLE IF NOT EXISTS matching_dispatch_policies (
    id UUID PRIMARY KEY,
    policy_name VARCHAR(100) NOT NULL UNIQUE,
    max_search_radius_km DOUBLE PRECISION NOT NULL DEFAULT 10.0,
    offer_timeout_seconds INT NOT NULL DEFAULT 30,
    max_retry_limit INT NOT NULL DEFAULT 3,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
