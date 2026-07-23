-- V6__Hyper_Memory_Engine.sql
-- Schema additions for Hyper Memory customer preferences and AI memory persistence

CREATE TABLE memories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    memory_id UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    memory_type VARCHAR(50) NOT NULL,
    key VARCHAR(120) NOT NULL,
    value VARCHAR(500) NOT NULL,
    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.8,
    last_used_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    usage_count BIGINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    CONSTRAINT fk_memories_customer FOREIGN KEY (customer_id) REFERENCES users(id)
);

CREATE INDEX idx_memories_customer_active ON memories(customer_id, active);
CREATE INDEX idx_memories_customer_type ON memories(customer_id, memory_type);
CREATE INDEX idx_memories_last_used_at ON memories(last_used_at);
