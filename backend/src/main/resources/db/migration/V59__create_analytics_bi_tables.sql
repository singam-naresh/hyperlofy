-- V59: Create Analytics, Reporting & Business Intelligence Tables (Event Ingestion, KPI Engine, Scheduled Reports & Executive Dashboards)

-- 1. Analytics Events Table
CREATE TABLE IF NOT EXISTS analytics_events (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    source_service VARCHAR(50) NOT NULL,
    entity_id UUID,
    payload TEXT,
    captured_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_aevt_type ON analytics_events(event_type);
CREATE INDEX IF NOT EXISTS idx_aevt_captured ON analytics_events(captured_at);

-- 2. Analytics KPIs Table
CREATE TABLE IF NOT EXISTS analytics_kpis (
    id UUID PRIMARY KEY,
    kpi_code VARCHAR(100) NOT NULL UNIQUE,
    kpi_name VARCHAR(150) NOT NULL,
    metric_value NUMERIC(16,4) NOT NULL DEFAULT 0.0000,
    unit VARCHAR(30) NOT NULL DEFAULT 'INR', -- INR, PERCENTAGE, SECONDS, COUNT
    period_code VARCHAR(30) NOT NULL DEFAULT 'REALTIME',
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Analytics Reports Table
CREATE TABLE IF NOT EXISTS analytics_reports (
    id UUID PRIMARY KEY,
    report_name VARCHAR(150) NOT NULL,
    report_type VARCHAR(50) NOT NULL, -- OPERATIONAL, FINANCIAL, MERCHANT, DRIVER, EXECUTIVE
    format VARCHAR(20) NOT NULL DEFAULT 'CSV', -- CSV, EXCEL, PDF
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    download_url TEXT,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Analytics Dashboards Table
CREATE TABLE IF NOT EXISTS analytics_dashboards (
    id UUID PRIMARY KEY,
    dashboard_key VARCHAR(100) NOT NULL UNIQUE,
    dashboard_title VARCHAR(150) NOT NULL,
    config_json TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
