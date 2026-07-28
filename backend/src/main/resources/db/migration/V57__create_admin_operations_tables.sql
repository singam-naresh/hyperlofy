-- V57: Create Admin & Operations Platform Tables (Operational Control Center, Feature Flags, Incident & Case Management)

-- 1. Admin Cases Table
CREATE TABLE IF NOT EXISTS admin_cases (
    id UUID PRIMARY KEY,
    case_number VARCHAR(100) NOT NULL UNIQUE,
    subject VARCHAR(255) NOT NULL,
    customer_id UUID,
    order_id UUID,
    priority VARCHAR(30) NOT NULL DEFAULT 'MEDIUM', -- LOW, MEDIUM, HIGH, URGENT
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN', -- OPEN, IN_PROGRESS, RESOLVED, CLOSED
    assigned_to VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_acase_cust ON admin_cases(customer_id);
CREATE INDEX IF NOT EXISTS idx_acase_status ON admin_cases(status);

-- 2. Admin Incidents Table
CREATE TABLE IF NOT EXISTS admin_incidents (
    id UUID PRIMARY KEY,
    incident_number VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    incident_type VARCHAR(50) NOT NULL, -- SYSTEM_OUTAGE, DRIVER_ACCIDENT, MERCHANT_FRAUD
    severity VARCHAR(30) NOT NULL DEFAULT 'SEV2', -- SEV1, SEV2, SEV3
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    reported_by VARCHAR(100) NOT NULL,
    resolution_notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Admin Feature Flags Table
CREATE TABLE IF NOT EXISTS admin_feature_flags (
    id UUID PRIMARY KEY,
    flag_key VARCHAR(100) NOT NULL UNIQUE,
    flag_name VARCHAR(150) NOT NULL,
    is_enabled BOOLEAN DEFAULT FALSE,
    rollout_percentage INT NOT NULL DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Admin Actions Table (Immutable Operations Audit Trail)
CREATE TABLE IF NOT EXISTS admin_actions (
    id UUID PRIMARY KEY,
    admin_user VARCHAR(100) NOT NULL,
    action_type VARCHAR(50) NOT NULL, -- REASSIGN_ORDER, APPROVE_MERCHANT, SUSPEND_DRIVER, TOGGLE_FLAG
    target_id UUID,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
