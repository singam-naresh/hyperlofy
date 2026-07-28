-- V42: Create Matching Engine Tables (Intelligent Driver Assignment & Scoring)

-- 1. Matching Requests Table
CREATE TABLE IF NOT EXISTS matching_requests (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    order_type VARCHAR(40) NOT NULL, -- MARKETPLACE, BUY_FOR_ME, PICKUP_DROP
    pickup_latitude DOUBLE PRECISION NOT NULL,
    pickup_longitude DOUBLE PRECISION NOT NULL,
    drop_latitude DOUBLE PRECISION NOT NULL,
    drop_longitude DOUBLE PRECISION NOT NULL,
    priority VARCHAR(20) DEFAULT 'NORMAL',
    status VARCHAR(30) NOT NULL DEFAULT 'MATCH_REQUESTED',
    assigned_driver_id UUID,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_match_order ON matching_requests(order_id);
CREATE INDEX IF NOT EXISTS idx_match_status ON matching_requests(status);

-- 2. Matching Candidates Table
CREATE TABLE IF NOT EXISTS matching_candidates (
    id UUID PRIMARY KEY,
    matching_request_id UUID NOT NULL REFERENCES matching_requests(id),
    driver_id UUID NOT NULL,
    distance_km DOUBLE PRECISION NOT NULL,
    eta_minutes INT NOT NULL,
    matching_score DOUBLE PRECISION NOT NULL,
    rank_position INT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_cand_req ON matching_candidates(matching_request_id);

-- 3. Matching Assignments Table
CREATE TABLE IF NOT EXISTS matching_assignments (
    id UUID PRIMARY KEY,
    matching_request_id UUID NOT NULL REFERENCES matching_requests(id),
    driver_id UUID NOT NULL,
    offer_status VARCHAR(30) NOT NULL DEFAULT 'OFFER_SENT', -- OFFER_SENT, ACCEPTED, REJECTED, TIMED_OUT
    offer_sent_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_assign_req ON matching_assignments(matching_request_id);
CREATE INDEX IF NOT EXISTS idx_assign_driver ON matching_assignments(driver_id);

-- 4. Matching History Table
CREATE TABLE IF NOT EXISTS matching_history (
    id UUID PRIMARY KEY,
    matching_request_id UUID NOT NULL REFERENCES matching_requests(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    change_reason VARCHAR(255),
    changed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
