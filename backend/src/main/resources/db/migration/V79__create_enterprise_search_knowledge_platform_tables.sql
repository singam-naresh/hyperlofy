-- V79: Enterprise Search, Knowledge Platform & Intelligent Discovery
-- SearchIndex, SearchDocument, KnowledgeArticle, KnowledgeCategory,
-- SearchAnalytics, SearchSuggestion, VectorEmbedding

-- 1. Search Indexes Table (Index Lifecycle Management — Hot/Warm/Cold)
CREATE TABLE IF NOT EXISTS search_indexes (
    id UUID PRIMARY KEY,
    index_name VARCHAR(150) NOT NULL UNIQUE,
    index_alias VARCHAR(150),
    domain VARCHAR(80) NOT NULL, -- MERCHANTS, PRODUCTS, ORDERS, CUSTOMERS, KNOWLEDGE, DOCUMENTS, WORKFLOWS, CASES, AUDIT
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, REINDEXING, WARM, COLD, ARCHIVED
    document_count BIGINT NOT NULL DEFAULT 0,
    size_bytes BIGINT NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 1,
    tenant_id UUID,
    last_reindexed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_si_domain ON search_indexes(domain);
CREATE INDEX IF NOT EXISTS idx_si_tenant ON search_indexes(tenant_id);
CREATE INDEX IF NOT EXISTS idx_si_status ON search_indexes(status);

-- 2. Search Documents Table (Indexed Document Registry with Full-Text & Vector Metadata)
CREATE TABLE IF NOT EXISTS search_documents (
    id UUID PRIMARY KEY,
    index_id UUID NOT NULL REFERENCES search_indexes(id),
    doc_external_id VARCHAR(150) NOT NULL, -- External ID from source domain (e.g. merchant UUID)
    doc_type VARCHAR(80) NOT NULL, -- MERCHANT, PRODUCT, ORDER, KNOWLEDGE_ARTICLE, DOCUMENT, CASE
    title VARCHAR(500) NOT NULL,
    body_excerpt TEXT, -- Indexed content excerpt for full-text search
    embedding_vector TEXT, -- Serialized float vector for semantic/vector search (e.g. base64 or JSON array)
    embedding_model VARCHAR(100) DEFAULT 'gemini-text-embedding', -- Embedding provider
    tags VARCHAR(500),
    category VARCHAR(100),
    tenant_id UUID,
    relevance_score NUMERIC(8,4) DEFAULT 1.0000,
    is_published BOOLEAN DEFAULT TRUE,
    source_url VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sd_index ON search_documents(index_id);
CREATE INDEX IF NOT EXISTS idx_sd_type ON search_documents(doc_type);
CREATE INDEX IF NOT EXISTS idx_sd_tenant ON search_documents(tenant_id);
CREATE INDEX IF NOT EXISTS idx_sd_ext ON search_documents(doc_external_id);

-- Full-text search index on title and body
CREATE INDEX IF NOT EXISTS idx_sd_fts ON search_documents USING GIN (to_tsvector('english', title || ' ' || COALESCE(body_excerpt, '')));

-- 3. Knowledge Articles Table (Enterprise Knowledge Management Platform)
CREATE TABLE IF NOT EXISTS knowledge_articles (
    id UUID PRIMARY KEY,
    article_key VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(500) NOT NULL,
    article_type VARCHAR(80) NOT NULL, -- SOP, GUIDE, POLICY, FAQ, RUNBOOK, PLAYBOOK, DOCUMENT, ENGINEERING
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT', -- DRAFT, REVIEW, PUBLISHED, ARCHIVED, DEPRECATED
    content TEXT NOT NULL,
    content_summary TEXT,
    category_id UUID,
    author_user_id UUID NOT NULL,
    reviewer_user_id UUID,
    published_at TIMESTAMP WITH TIME ZONE,
    version INTEGER NOT NULL DEFAULT 1,
    tags VARCHAR(500),
    view_count INTEGER NOT NULL DEFAULT 0,
    helpful_votes INTEGER NOT NULL DEFAULT 0,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ka_type ON knowledge_articles(article_type);
CREATE INDEX IF NOT EXISTS idx_ka_status ON knowledge_articles(status);
CREATE INDEX IF NOT EXISTS idx_ka_tenant ON knowledge_articles(tenant_id);
CREATE INDEX IF NOT EXISTS idx_ka_fts ON knowledge_articles USING GIN (to_tsvector('english', title || ' ' || content_summary));

-- 4. Knowledge Categories Table
CREATE TABLE IF NOT EXISTS knowledge_categories (
    id UUID PRIMARY KEY,
    category_key VARCHAR(100) NOT NULL UNIQUE,
    category_name VARCHAR(150) NOT NULL,
    parent_id UUID REFERENCES knowledge_categories(id),
    icon VARCHAR(50),
    sort_order INTEGER NOT NULL DEFAULT 0,
    article_count INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Search Analytics Table (Query Performance, Zero-Result Detection, CTR)
CREATE TABLE IF NOT EXISTS search_analytics (
    id UUID PRIMARY KEY,
    query_text VARCHAR(500) NOT NULL,
    domain VARCHAR(80),
    result_count INTEGER NOT NULL DEFAULT 0,
    is_zero_result BOOLEAN DEFAULT FALSE,
    selected_doc_id VARCHAR(150), -- Which document was clicked (CTR tracking)
    response_time_ms INTEGER NOT NULL DEFAULT 0,
    search_type VARCHAR(30) NOT NULL DEFAULT 'FULL_TEXT', -- FULL_TEXT, SEMANTIC, HYBRID, FACETED
    user_id UUID,
    tenant_id UUID,
    session_id VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sa_query ON search_analytics(query_text);
CREATE INDEX IF NOT EXISTS idx_sa_domain ON search_analytics(domain);
CREATE INDEX IF NOT EXISTS idx_sa_zero ON search_analytics(is_zero_result);
CREATE INDEX IF NOT EXISTS idx_sa_tenant ON search_analytics(tenant_id);

-- 6. Search Suggestions Table (Autocomplete, Trending, Synonyms)
CREATE TABLE IF NOT EXISTS search_suggestions (
    id UUID PRIMARY KEY,
    suggestion_text VARCHAR(300) NOT NULL,
    suggestion_type VARCHAR(30) NOT NULL DEFAULT 'AUTOCOMPLETE', -- AUTOCOMPLETE, TRENDING, SYNONYM, RELATED, POPULAR
    domain VARCHAR(80),
    frequency INTEGER NOT NULL DEFAULT 1,
    synonym_for VARCHAR(300),
    is_active BOOLEAN DEFAULT TRUE,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ss_text ON search_suggestions(suggestion_text);
CREATE INDEX IF NOT EXISTS idx_ss_type ON search_suggestions(suggestion_type);

-- 7. Vector Embeddings Table (RAG Foundation — Chunk-Level Embeddings for Semantic Retrieval)
CREATE TABLE IF NOT EXISTS vector_embeddings (
    id UUID PRIMARY KEY,
    source_doc_id VARCHAR(150) NOT NULL, -- External doc ID this embedding belongs to
    source_doc_type VARCHAR(80) NOT NULL, -- KNOWLEDGE_ARTICLE, DOCUMENT, PRODUCT, MERCHANT
    chunk_index INTEGER NOT NULL DEFAULT 0, -- Position of chunk in source doc
    chunk_text TEXT NOT NULL, -- Text chunk used to generate embedding
    embedding_vector TEXT NOT NULL, -- Serialized float vector (base64 or JSON)
    embedding_model VARCHAR(100) NOT NULL DEFAULT 'gemini-text-embedding',
    embedding_dimension INTEGER NOT NULL DEFAULT 768,
    similarity_score NUMERIC(8,6) DEFAULT 0.000000, -- Set during retrieval
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ve_source ON vector_embeddings(source_doc_id);
CREATE INDEX IF NOT EXISTS idx_ve_type ON vector_embeddings(source_doc_type);
CREATE INDEX IF NOT EXISTS idx_ve_tenant ON vector_embeddings(tenant_id);
