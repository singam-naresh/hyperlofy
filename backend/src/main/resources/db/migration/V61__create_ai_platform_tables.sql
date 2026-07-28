-- V61: Create AI & Platform Intelligence Services Tables (Model Routing, Recommendation Engine, Prompt Management, & Inference Logging)

-- 1. AI Prompts Table
CREATE TABLE IF NOT EXISTS ai_prompts (
    id UUID PRIMARY KEY,
    prompt_key VARCHAR(100) NOT NULL UNIQUE,
    prompt_name VARCHAR(150) NOT NULL,
    template_text TEXT NOT NULL,
    version VARCHAR(30) NOT NULL DEFAULT 'v1.0.0',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. AI Model Registry Table
CREATE TABLE IF NOT EXISTS ai_model_registry (
    id UUID PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL UNIQUE,
    provider VARCHAR(50) NOT NULL, -- GEMINI, OPENAI, ANTHROPIC, LOCAL
    endpoint_url VARCHAR(255),
    is_primary BOOLEAN DEFAULT TRUE,
    latency_ms INT DEFAULT 120,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. AI Recommendations Table
CREATE TABLE IF NOT EXISTS ai_recommendations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    recommendation_type VARCHAR(50) NOT NULL, -- PRODUCT, MERCHANT, UPSELL, CROSS_SELL
    recommended_entity_id UUID NOT NULL,
    confidence_score NUMERIC(5,4) NOT NULL DEFAULT 0.9200,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_airec_user ON ai_recommendations(user_id);

-- 4. AI Inference Logs Table
CREATE TABLE IF NOT EXISTS ai_inference_logs (
    id UUID PRIMARY KEY,
    model_name VARCHAR(100) NOT NULL,
    prompt_key VARCHAR(100),
    user_id UUID,
    token_count INT DEFAULT 256,
    execution_time_ms INT DEFAULT 145,
    status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);
