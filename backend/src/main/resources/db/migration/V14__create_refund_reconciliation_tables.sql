-- V14: Create refund_reconciliations table for Refund & Settlement Reconciliation Engine

CREATE TABLE refund_reconciliations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    refund_type VARCHAR(30) NOT NULL,
    escrow_status_at_refund VARCHAR(30) NOT NULL,
    total_order_amount DECIMAL(12, 2) NOT NULL,
    refund_amount DECIMAL(12, 2) NOT NULL,
    merchant_adjustment DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    agent_adjustment DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    platform_adjustment DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    reason VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_refund_reconciliations_order_id ON refund_reconciliations(order_id);
CREATE INDEX idx_refund_reconciliations_status ON refund_reconciliations(status);
