-- V71: Create Enterprise Data Platform Tables (Data Pipelines, Real-Time Streaming Jobs, Iceberg Lakehouse Tables, & Feature Store)

-- 1. Data Pipelines Table
CREATE TABLE IF NOT EXISTS data_pipelines (
    id UUID PRIMARY KEY,
    pipeline_code VARCHAR(100) NOT NULL UNIQUE,
    pipeline_name VARCHAR(150) NOT NULL,
    pipeline_type VARCHAR(50) NOT NULL, -- STREAMING, BATCH, CDC
    source_system VARCHAR(100) NOT NULL,
    target_layer VARCHAR(30) NOT NULL DEFAULT 'BRONZE', -- BRONZE, SILVER, GOLD
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Stream Jobs Table (Kafka Streams / Apache Flink)
CREATE TABLE IF NOT EXISTS stream_jobs (
    id UUID PRIMARY KEY,
    job_name VARCHAR(150) NOT NULL UNIQUE,
    engine_type VARCHAR(50) NOT NULL DEFAULT 'KAFKA_STREAMS', -- KAFKA_STREAMS, FLINK, SPARK_STREAMING
    input_topic VARCHAR(150) NOT NULL,
    output_topic VARCHAR(150) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'RUNNING',
    throughput_eps INT NOT NULL DEFAULT 5000,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Lakehouse Tables Table (Apache Iceberg / Parquet)
CREATE TABLE IF NOT EXISTS lakehouse_tables (
    id UUID PRIMARY KEY,
    table_name VARCHAR(150) NOT NULL UNIQUE,
    schema_namespace VARCHAR(100) NOT NULL DEFAULT 'hyperlofy_lakehouse',
    lakehouse_layer VARCHAR(30) NOT NULL, -- BRONZE, SILVER, GOLD
    format VARCHAR(30) NOT NULL DEFAULT 'ICEBERG_PARQUET',
    total_records BIGINT NOT NULL DEFAULT 0,
    size_bytes BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 4. Feature Store Table (Online/Offline ML Features)
CREATE TABLE IF NOT EXISTS feature_store (
    id UUID PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL, -- USER, MERCHANT, DRIVER, ORDER
    entity_id VARCHAR(100) NOT NULL,
    feature_name VARCHAR(100) NOT NULL,
    feature_value VARCHAR(255) NOT NULL,
    feature_version VARCHAR(30) NOT NULL DEFAULT 'v1',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_fs_entity ON feature_store(entity_type, entity_id);
