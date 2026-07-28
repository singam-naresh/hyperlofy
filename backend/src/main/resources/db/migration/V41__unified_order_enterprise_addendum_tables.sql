-- V41: Unified Order Enterprise Addendum Tables (Saga Orchestration, Outbox, Inbox, Compensations, & Replays)

-- 1. Order Sagas Table
CREATE TABLE IF NOT EXISTS order_sagas (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    saga_name VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'STARTED', -- STARTED, COMPLETED, COMPENSATING, FAILED
    current_step INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_saga_order ON order_sagas(order_id);

-- 2. Order Saga Steps Table
CREATE TABLE IF NOT EXISTS order_saga_steps (
    id UUID PRIMARY KEY,
    saga_id UUID NOT NULL REFERENCES order_sagas(id),
    step_name VARCHAR(100) NOT NULL,
    step_order INT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS', -- PENDING, SUCCESS, FAILED, COMPENSATED
    error_message TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Order Compensations Table
CREATE TABLE IF NOT EXISTS order_compensations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    compensation_action VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- SUBMITTED, COMPLETED, FAILED
    reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Transactional Outbox Table
CREATE TABLE IF NOT EXISTS order_outbox (
    id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    is_published BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMP WITH TIME ZONE,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_outbox_pub ON order_outbox(is_published);

-- 5. Transactional Inbox Table
CREATE TABLE IF NOT EXISTS order_inbox (
    id UUID PRIMARY KEY,
    message_id VARCHAR(128) NOT NULL UNIQUE,
    source_service VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    is_processed BOOLEAN DEFAULT TRUE,
    processed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_uo_inbox_msg ON order_inbox(message_id);

-- 6. Order Relationships Table
CREATE TABLE IF NOT EXISTS order_relationships (
    id UUID PRIMARY KEY,
    parent_order_id UUID NOT NULL REFERENCES orders(id),
    child_order_id UUID NOT NULL REFERENCES orders(id),
    relationship_type VARCHAR(40) NOT NULL, -- PARENT_CHILD, SPLIT, MERGED, REPLACEMENT, RETURN
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 7. Event Replays Table
CREATE TABLE IF NOT EXISTS order_replays (
    id UUID PRIMARY KEY,
    replay_code VARCHAR(50) NOT NULL UNIQUE,
    target_order_id UUID REFERENCES orders(id),
    replayed_events_count INT NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    initiated_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
