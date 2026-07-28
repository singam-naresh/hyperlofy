-- V60: Create Analytics Platform Enterprise Addendum Tables (Predictive Analytics, Anomaly Detection, Executive Scorecards, & AI Insights)

-- 1. Analytics Predictions Table
CREATE TABLE IF NOT EXISTS analytics_predictions (
    id UUID PRIMARY KEY,
    prediction_target VARCHAR(100) NOT NULL, -- DEMAND, REVENUE, DRIVER_DEMAND, DELIVERY_TIME
    model_version VARCHAR(50) NOT NULL DEFAULT 'v1.0.0',
    predicted_value NUMERIC(16,4) NOT NULL DEFAULT 0.0000,
    confidence_score NUMERIC(5,4) NOT NULL DEFAULT 0.9500,
    forecast_horizon VARCHAR(30) NOT NULL DEFAULT '24_HOURS',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Analytics Anomalies Table
CREATE TABLE IF NOT EXISTS analytics_anomalies (
    id UUID PRIMARY KEY,
    metric_code VARCHAR(100) NOT NULL,
    anomaly_type VARCHAR(50) NOT NULL, -- REVENUE_DROP, REFUND_SPIKE, SETTLEMENT_FAILURE, SLA_DEGRADATION
    severity VARCHAR(30) NOT NULL DEFAULT 'MEDIUM',
    baseline_value NUMERIC(16,4) NOT NULL DEFAULT 0.0000,
    observed_value NUMERIC(16,4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Analytics Scorecards Table
CREATE TABLE IF NOT EXISTS analytics_scorecards (
    id UUID PRIMARY KEY,
    scorecard_role VARCHAR(50) NOT NULL UNIQUE, -- CEO, COO, CFO, OPERATIONS, MERCHANT_SUCCESS
    overall_score NUMERIC(5,2) NOT NULL DEFAULT 95.00,
    grade VARCHAR(5) NOT NULL DEFAULT 'A+',
    metrics_summary_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Analytics AI Insights Table
CREATE TABLE IF NOT EXISTS analytics_ai_insights (
    id UUID PRIMARY KEY,
    insight_category VARCHAR(100) NOT NULL,
    title VARCHAR(255) NOT NULL,
    recommendation_text TEXT NOT NULL,
    impact_score VARCHAR(30) NOT NULL DEFAULT 'HIGH',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
