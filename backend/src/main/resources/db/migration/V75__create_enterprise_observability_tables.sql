-- V75: Create Enterprise Observability, AIOps & Autonomous Operations Tables (Telemetry Events, Distributed Traces, Anomaly Reports, & Autonomous Runbook Executions)

-- 1. Telemetry Events Table
CREATE TABLE IF NOT EXISTS telemetry_events (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- METRIC, LOG, SPAN, SYNTHETIC
    metric_name VARCHAR(150) NOT NULL,
    metric_value NUMERIC(16,4) NOT NULL,
    correlation_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_te_correlation ON telemetry_events(correlation_id);

-- 2. Distributed Traces Table (OpenTelemetry / Jaeger / Tempo)
CREATE TABLE IF NOT EXISTS distributed_traces (
    id UUID PRIMARY KEY,
    trace_id VARCHAR(100) NOT NULL,
    span_id VARCHAR(100) NOT NULL,
    parent_span_id VARCHAR(100),
    service_name VARCHAR(100) NOT NULL,
    operation_name VARCHAR(150) NOT NULL,
    duration_ms BIGINT NOT NULL DEFAULT 0,
    status_code VARCHAR(30) NOT NULL DEFAULT 'OK', -- OK, ERROR, UNSET
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dt_trace ON distributed_traces(trace_id);

-- 3. Anomaly Reports Table (AIOps Anomaly Detection)
CREATE TABLE IF NOT EXISTS anomaly_reports (
    id UUID PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    anomaly_type VARCHAR(50) NOT NULL, -- LATENCY_SPIKE, ERROR_BURST, MEMORY_LEAK
    severity VARCHAR(30) NOT NULL DEFAULT 'CRITICAL', -- CRITICAL, HIGH, MEDIUM
    confidence_score NUMERIC(5,2) NOT NULL DEFAULT 98.50,
    status VARCHAR(30) NOT NULL DEFAULT 'DETECTED', -- DETECTED, INVESTIGATING, RESOLVED
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Autonomous Runbook Executions Table (Self-Healing Operations)
CREATE TABLE IF NOT EXISTS runbook_executions (
    id UUID PRIMARY KEY,
    runbook_name VARCHAR(150) NOT NULL,
    target_service VARCHAR(100) NOT NULL,
    action_type VARCHAR(50) NOT NULL, -- POD_RESTART, TRAFFIC_SHIFT, CAPACITY_SCALE
    execution_status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS', -- SUCCESS, FAILED, IN_PROGRESS
    execution_time_ms BIGINT NOT NULL DEFAULT 1200,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
