-- Enable UUID Extension in PostgreSQL
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(15) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE NOT NULL,
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    role VARCHAR(30) NOT NULL, -- CUSTOMER, AGENT, ADMIN, SUPER_ADMIN
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

-- Index on user email & phone for fast lookups
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone_number);

-- Refresh Token Table (to manage secure login sessions across devices)
CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(512) UNIQUE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN DEFAULT FALSE NOT NULL,
    
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_refresh_token_value ON refresh_tokens(token);

-- Customer Profiles Table (Hexagonal customer entity)
CREATE TABLE customer_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    default_delivery_address TEXT,
    gps_latitude DOUBLE PRECISION,
    gps_longitude DOUBLE PRECISION,
    preferred_payment_method VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

-- Agent Profiles Table (With Aadhaar/PAN Verification Fields & status transitions)
CREATE TABLE agent_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    vehicle_type VARCHAR(50) NOT NULL, -- BICYCLE, MOTORCYCLE, CAR, ELECTRIC_SCOOTER
    vehicle_number VARCHAR(30),
    current_gps_latitude DOUBLE PRECISION,
    current_gps_longitude DOUBLE PRECISION,
    is_available BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Verification details
    pan_number VARCHAR(10) UNIQUE NOT NULL,
    pan_doc_url VARCHAR(255),
    aadhaar_number VARCHAR(12) UNIQUE NOT NULL,
    aadhaar_doc_url VARCHAR(255),
    profile_image_url VARCHAR(255),
    
    -- Verification workflow states: PENDING, APPROVED, REJECTED, SUSPENDED
    verification_status VARCHAR(30) DEFAULT 'PENDING' NOT NULL,
    rejection_reason TEXT,
    suspended_at TIMESTAMP WITH TIME ZONE,
    suspension_reason TEXT,
    
    -- Soft delete
    deleted BOOLEAN DEFAULT FALSE NOT NULL,
    
    -- Audit fields
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL,
    updated_by VARCHAR(100) DEFAULT 'SYSTEM' NOT NULL
);

CREATE INDEX idx_agent_verification_status ON agent_profiles(verification_status);

-- Agent Verification Log Table for audit trail of administrative state transitions
CREATE TABLE agent_verification_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL REFERENCES agent_profiles(id) ON DELETE CASCADE,
    admin_user_id UUID NOT NULL REFERENCES users(id),
    previous_status VARCHAR(30) NOT NULL,
    new_status VARCHAR(30) NOT NULL,
    remarks TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);
