-- V8__Hyper_Recommendation_Engine.sql
-- Schema additions for Hyper Recommendation records

CREATE TABLE recommendations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    recommendation_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    conversation_id UUID,
    order_draft_id UUID,
    recommended_item VARCHAR(300) NOT NULL,
    reason VARCHAR(80) NOT NULL,
    recommendation_type VARCHAR(80) NOT NULL,
    score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    accepted BOOLEAN NOT NULL DEFAULT FALSE,
    dismissed BOOLEAN NOT NULL DEFAULT FALSE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT fk_recommendations_customer FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE INDEX idx_recommendations_customer ON recommendations(customer_id);
CREATE INDEX idx_recommendations_conversation ON recommendations(conversation_id);
CREATE INDEX idx_recommendations_order_draft ON recommendations(order_draft_id);
CREATE INDEX idx_recommendations_accepted ON recommendations(accepted);
CREATE INDEX idx_recommendations_dismissed ON recommendations(dismissed);
