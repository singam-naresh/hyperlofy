-- V45: Live Tracking Engine Enterprise Addendum Tables (Trip Replay, ETA History, Privacy, & Fraud Scores)

-- 1. Tracking ETA History Table
CREATE TABLE IF NOT EXISTS tracking_eta_history (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    calculated_eta TIMESTAMP WITH TIME ZONE NOT NULL,
    remaining_distance_km DOUBLE PRECISION NOT NULL,
    remaining_duration_minutes INT NOT NULL,
    confidence_score DOUBLE PRECISION NOT NULL DEFAULT 95.0,
    eta_version INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_tetah_session ON tracking_eta_history(tracking_session_id);

-- 2. Tracking Trip Replays Table
CREATE TABLE IF NOT EXISTS tracking_trip_replays (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    replay_data_json TEXT NOT NULL,
    total_stops_detected INT DEFAULT 0,
    idle_duration_minutes INT DEFAULT 0,
    average_speed_kmh DOUBLE PRECISION DEFAULT 25.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Tracking Offline Sessions Table
CREATE TABLE IF NOT EXISTS tracking_offline_sessions (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    buffered_points_count INT NOT NULL DEFAULT 0,
    sync_status VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, COMPLETED, CONFLICT_RESOLVED
    synced_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Tracking Privacy Rules Table
CREATE TABLE IF NOT EXISTS tracking_privacy_rules (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    precision_mask_meters INT DEFAULT 50,
    enable_location_masking BOOLEAN DEFAULT FALSE,
    retention_days INT DEFAULT 30,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Tracking Fraud Scores Table
CREATE TABLE IF NOT EXISTS tracking_fraud_scores (
    id UUID PRIMARY KEY,
    tracking_session_id UUID NOT NULL REFERENCES tracking_sessions(id),
    driver_id UUID NOT NULL,
    fraud_type VARCHAR(50) NOT NULL, -- GPS_SPOOFING, MOCK_GPS, IMPOSSIBLE_SPEED
    risk_score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    action_taken VARCHAR(50) DEFAULT 'ALERT_GENERATED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
