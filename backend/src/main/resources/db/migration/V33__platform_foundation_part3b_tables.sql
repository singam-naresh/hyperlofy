-- V33: Platform Foundation Part 3B Tables (Backup, Restore, & PITR)

-- 1. Backup Jobs Table
CREATE TABLE IF NOT EXISTS backup_jobs (
    id UUID PRIMARY KEY,
    job_name VARCHAR(100) NOT NULL,
    backup_type VARCHAR(30) NOT NULL DEFAULT 'FULL', -- FULL, INCREMENTAL, WAL, SNAPSHOT
    target_system VARCHAR(50) NOT NULL,            -- POSTGRESQL, REDIS, CONFIGURATION
    schedule_cron VARCHAR(50) DEFAULT '0 0 * * *',
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- PENDING, IN_PROGRESS, COMPLETED, FAILED
    last_run_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    next_run_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Backup Catalog Table
CREATE TABLE IF NOT EXISTS backup_catalog (
    id UUID PRIMARY KEY,
    backup_job_id UUID,
    backup_type VARCHAR(30) NOT NULL,
    storage_location VARCHAR(255) NOT NULL,
    file_size_bytes BIGINT NOT NULL DEFAULT 0,
    checksum_sha256 VARCHAR(64) NOT NULL,
    encryption_algorithm VARCHAR(30) DEFAULT 'AES-256',
    retention_days INT NOT NULL DEFAULT 30,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    is_verified BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_bck_catalog_job ON backup_catalog(backup_job_id);

-- 3. Restore Jobs Table
CREATE TABLE IF NOT EXISTS restore_jobs (
    id UUID PRIMARY KEY,
    backup_catalog_id UUID NOT NULL,
    restore_target VARCHAR(100) NOT NULL,
    restore_status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED', -- IN_PROGRESS, COMPLETED, FAILED
    initiated_by VARCHAR(100) NOT NULL,
    recovery_time_seconds INT DEFAULT 0,
    recovery_confidence_percentage DOUBLE PRECISION DEFAULT 99.9,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. PITR History Table
CREATE TABLE IF NOT EXISTS pitr_history (
    id UUID PRIMARY KEY,
    target_time TIMESTAMP WITH TIME ZONE NOT NULL,
    target_lsn VARCHAR(100),
    timeline_id INT DEFAULT 1,
    recovery_status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    executed_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
