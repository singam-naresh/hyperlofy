-- V22: Create AI Demand Forecasting & Merchant Intelligence Tables

-- 1. Merchant Demand Forecasts Table
CREATE TABLE IF NOT EXISTS merchant_demand_forecasts (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    forecast_type VARCHAR(50) NOT NULL, -- HOURLY, DAILY, WEEKLY, MONTHLY
    projected_order_volume INT NOT NULL DEFAULT 0,
    projected_revenue DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    confidence_score DOUBLE PRECISION DEFAULT 0.85,
    forecast_date VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_forecast_merchant ON merchant_demand_forecasts(merchant_id);

-- 2. Merchant Intelligence Snapshots Table
CREATE TABLE IF NOT EXISTS merchant_intelligence_snapshots (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL UNIQUE,
    growth_score DOUBLE PRECISION DEFAULT 1.0,
    health_score DOUBLE PRECISION DEFAULT 1.0,
    repeat_customer_rate DOUBLE PRECISION DEFAULT 0.0,
    peak_ordering_hours VARCHAR(100),
    top_selling_product_ids TEXT,
    low_stock_product_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
