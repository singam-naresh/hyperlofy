-- V19: Create Real-Time Event Audit Tables

CREATE TABLE IF NOT EXISTS system_event_logs (
    id UUID PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100),
    payload_json TEXT,
    actor_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_system_event_type ON system_event_logs(event_type);
CREATE INDEX IF NOT EXISTS idx_system_event_aggregate ON system_event_logs(aggregate_id);
