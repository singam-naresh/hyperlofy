-- V10__Decision_History_Explainability.sql
-- Add persistent decision history explainability records for AI execution replay/debugging.

CREATE TABLE decision_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    execution_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    conversation_id UUID,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    intent VARCHAR(120),
    entities VARCHAR(500),
    reasoning_summary TEXT,
    recommendation_summary TEXT,
    verification_summary TEXT,
    selected_merchant VARCHAR(200),
    selected_helper VARCHAR(200),
    estimated_cost DOUBLE PRECISION,
    actual_cost DOUBLE PRECISION,
    confidence DOUBLE PRECISION,
    decision_trace TEXT,
    execution_status VARCHAR(60),
    memory_references TEXT,
    feedback_references TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT fk_decision_history_customer FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE INDEX idx_decision_history_execution ON decision_history(execution_id);
CREATE INDEX idx_decision_history_customer ON decision_history(customer_id);
CREATE INDEX idx_decision_history_conversation ON decision_history(conversation_id);
CREATE INDEX idx_decision_history_timestamp ON decision_history(timestamp);
