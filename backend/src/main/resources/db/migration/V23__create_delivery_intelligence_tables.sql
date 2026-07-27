-- V23: Create AI Delivery Intelligence & ETA Prediction Tables

-- 1. ETA Predictions Table
CREATE TABLE IF NOT EXISTS eta_predictions (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    estimated_prep_minutes INT NOT NULL DEFAULT 15,
    estimated_travel_minutes INT NOT NULL DEFAULT 20,
    total_eta_minutes INT NOT NULL DEFAULT 35,
    confidence_score DOUBLE PRECISION DEFAULT 0.90,
    prediction_strategy VARCHAR(50) NOT NULL DEFAULT 'HYBRID_WEIGHTED_AVERAGE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_eta_order ON eta_predictions(order_id);

-- 2. Driver Intelligence Snapshots Table
CREATE TABLE IF NOT EXISTS driver_intelligence_snapshots (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL UNIQUE,
    acceptance_rate DOUBLE PRECISION DEFAULT 1.0,
    completion_rate DOUBLE PRECISION DEFAULT 1.0,
    average_speed_kmh DOUBLE PRECISION DEFAULT 25.0,
    reliability_score DOUBLE PRECISION DEFAULT 0.95,
    efficiency_score DOUBLE PRECISION DEFAULT 0.92,
    rating DOUBLE PRECISION DEFAULT 4.8,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
