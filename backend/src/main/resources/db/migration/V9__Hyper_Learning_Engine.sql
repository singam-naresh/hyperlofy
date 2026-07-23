-- V9__Hyper_Learning_Engine.sql
-- Schema additions for Hyper Learning and feedback records

CREATE TABLE learning_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    learning_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    conversation_id UUID,
    order_id UUID,
    merchant_id UUID,
    recommendation_id UUID,
    learning_type VARCHAR(60) NOT NULL,
    score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    recency DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    frequency DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    details TEXT,
    event_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT fk_learning_events_customer FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE INDEX idx_learning_events_customer ON learning_events(customer_id);
CREATE INDEX idx_learning_events_merchant ON learning_events(merchant_id);
CREATE INDEX idx_learning_events_recommendation ON learning_events(recommendation_id);
CREATE INDEX idx_learning_events_order ON learning_events(order_id);
CREATE INDEX idx_learning_events_type ON learning_events(learning_type);
CREATE INDEX idx_learning_events_event_at ON learning_events(event_at);
