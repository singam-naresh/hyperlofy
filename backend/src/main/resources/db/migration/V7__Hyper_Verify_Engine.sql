-- V7__Hyper_Verify_Engine.sql
-- Schema additions for Hyper Verify transaction verification records

CREATE TABLE verifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    verification_id UUID NOT NULL UNIQUE,
    order_id UUID NOT NULL,
    created_by_user_id UUID NOT NULL,
    verification_type VARCHAR(50) NOT NULL,
    verification_result VARCHAR(30) NOT NULL,
    payload TEXT NOT NULL,
    expected_value VARCHAR(500),
    expected_price NUMERIC(10,2),
    source_url VARCHAR(400),
    score DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    message VARCHAR(500),
    details TEXT,
    processed_at TIMESTAMP WITH TIME ZONE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT fk_verifications_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_verifications_created_by_user FOREIGN KEY (created_by_user_id) REFERENCES users(id)
);

CREATE INDEX idx_verifications_order ON verifications(order_id);
CREATE INDEX idx_verifications_type ON verifications(verification_type);
CREATE INDEX idx_verifications_result ON verifications(verification_result);
