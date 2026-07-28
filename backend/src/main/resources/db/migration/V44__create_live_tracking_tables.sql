-- V44: Create Live Tracking Engine Tables (Real-Time GPS Streaming & ETA Calculation)

-- 1. Tracking Sessions Table
CREATE TABLE IF NOT EXISTS tracking_sessions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    driver_id UUID NOT NULL,
    status VARCHAR(40) NOT NULL DEFAULT 'TRACKING_INITIALIZED',
    started_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tsess_order ON tracking_sessions(order_id);
CREATE INDEX IF NOT EXISTS idx_tsess_driver ON tracking_sessions(driver_id);

-- 2. Tracking Locations Table
CREATE TABLE IF NOT EXISTS tracking_locations (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    heading DOUBLE PRECISION DEFAULT 0.0,
    speed_kmh DOUBLE PRECISION DEFAULT 0.0,
    accuracy_meters DOUBLE PRECISION DEFAULT 5.0,
    device_timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tloc_session ON tracking_locations(tracking_session_id);

-- 3. Tracking ETA Table
CREATE TABLE IF NOT EXISTS tracking_eta (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    remaining_distance_km DOUBLE PRECISION NOT NULL,
    remaining_duration_minutes INT NOT NULL,
    calculated_eta TIMESTAMP WITH TIME ZONE NOT NULL,
    average_speed_kmh DOUBLE PRECISION DEFAULT 25.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_teta_session ON tracking_eta(tracking_session_id);

-- 4. Tracking Geofence Events Table
CREATE TABLE IF NOT EXISTS tracking_geofence_events (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    geofence_type VARCHAR(40) NOT NULL, -- PICKUP_GEOFENCE, DELIVERY_GEOFENCE
    event_type VARCHAR(20) NOT NULL, -- ENTERED, EXITED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Tracking Timeline Table
CREATE TABLE IF NOT EXISTS tracking_timeline (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    event_name VARCHAR(50) NOT NULL,
    description TEXT,
    event_time TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
