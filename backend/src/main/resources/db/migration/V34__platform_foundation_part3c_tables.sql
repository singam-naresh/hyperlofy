-- V34: Platform Foundation Part 3C Tables (Data Archival, Retention, Legal Hold, & Compliance)

-- 1. Retention Policies Table
CREATE TABLE IF NOT EXISTS retention_policies (
    id UUID PRIMARY KEY,
    policy_name VARCHAR(100) NOT NULL UNIQUE,
    data_classification VARCHAR(50) NOT NULL, -- TRANSACTIONAL, FINANCIAL, SECURITY, AUDIT
    retention_period_days INT NOT NULL DEFAULT 365,
    storage_tier VARCHAR(30) NOT NULL DEFAULT 'WARM', -- HOT, WARM, COLD, ARCHIVE
    auto_archive BOOLEAN DEFAULT TRUE,
    auto_purge BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Legal Holds Table
CREATE TABLE IF NOT EXISTS legal_holds (
    id UUID PRIMARY KEY,
    case_id VARCHAR(100) NOT NULL UNIQUE,
    target_table VARCHAR(100) NOT NULL,
    target_record_id VARCHAR(255) NOT NULL,
    reason TEXT NOT NULL,
    hold_owner VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    effective_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    expiration_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_lh_case ON legal_holds(case_id);
CREATE INDEX IF NOT EXISTS idx_lh_target ON legal_holds(target_table, target_record_id);

-- 3. Archive Catalog Table
CREATE TABLE IF NOT EXISTS archive_catalog (
    id UUID PRIMARY KEY,
    dataset_name VARCHAR(100) NOT NULL,
    archive_location VARCHAR(255) NOT NULL,
    record_count INT NOT NULL DEFAULT 0,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    checksum_sha256 VARCHAR(64) NOT NULL,
    encryption_algorithm VARCHAR(30) DEFAULT 'AES-256',
    compression_method VARCHAR(30) DEFAULT 'GZIP',
    storage_tier VARCHAR(30) DEFAULT 'COLD',
    has_legal_hold BOOLEAN DEFAULT FALSE,
    is_verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_arch_dataset ON archive_catalog(dataset_name);

-- 4. Archive Jobs Table
CREATE TABLE IF NOT EXISTS archive_jobs (
    id UUID PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    dataset_name VARCHAR(100) NOT NULL,
    archived_record_count INT DEFAULT 0,
    job_status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- IN_PROGRESS, COMPLETED, FAILED
    executed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
