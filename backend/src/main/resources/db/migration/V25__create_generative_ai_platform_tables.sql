-- V25: Create Generative AI Platform, LLM Infrastructure & RAG Tables

-- 1. Prompt Templates Table
CREATE TABLE IF NOT EXISTS prompt_templates (
    id UUID PRIMARY KEY,
    template_key VARCHAR(100) NOT NULL UNIQUE,
    version INT NOT NULL DEFAULT 1,
    category VARCHAR(50) NOT NULL DEFAULT 'GENERAL',
    system_prompt TEXT NOT NULL,
    user_prompt_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Knowledge Documents Table
CREATE TABLE IF NOT EXISTS knowledge_documents (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'FAQ',
    content TEXT NOT NULL,
    content_type VARCHAR(50) NOT NULL DEFAULT 'MARKDOWN',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 3. Knowledge Document Chunks & Vector Embeddings Table
CREATE TABLE IF NOT EXISTS knowledge_document_chunks (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    chunk_index INT NOT NULL DEFAULT 0,
    chunk_content TEXT NOT NULL,
    embedding_vector TEXT, -- Serialized vector representation
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_chunks_document ON knowledge_document_chunks(document_id);
