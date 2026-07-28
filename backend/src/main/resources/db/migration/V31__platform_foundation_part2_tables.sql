-- V31: Platform Foundation Part 2 Tables (i18n, Feature Flags, Secrets Management)

-- 1. Supported Languages Table
CREATE TABLE IF NOT EXISTS supported_languages (
    id UUID PRIMARY KEY,
    language_code VARCHAR(10) NOT NULL UNIQUE, -- en, hi, te, ta, kn, ml
    language_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Feature Flags Table
CREATE TABLE IF NOT EXISTS feature_flags (
    id UUID PRIMARY KEY,
    flag_key VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    flag_status VARCHAR(30) NOT NULL DEFAULT 'PRODUCTION', -- DRAFT, BETA, PRODUCTION, DEPRECATED
    is_enabled BOOLEAN DEFAULT TRUE,
    rollout_percentage INT DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ff_key ON feature_flags(flag_key);

-- 3. Feature Flag Audit Table
CREATE TABLE IF NOT EXISTS feature_flag_audit (
    id UUID PRIMARY KEY,
    flag_key VARCHAR(100) NOT NULL,
    old_status VARCHAR(30),
    new_status VARCHAR(30) NOT NULL,
    old_enabled BOOLEAN,
    new_enabled BOOLEAN NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Secret Rotation History Table
CREATE TABLE IF NOT EXISTS secret_rotation_history (
    id UUID PRIMARY KEY,
    secret_key VARCHAR(100) NOT NULL,
    version_id VARCHAR(100) NOT NULL,
    provider_name VARCHAR(50) NOT NULL DEFAULT 'ENVIRONMENT',
    rotated_by VARCHAR(100) NOT NULL,
    rotated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_secret_rot_key ON secret_rotation_history(secret_key);
