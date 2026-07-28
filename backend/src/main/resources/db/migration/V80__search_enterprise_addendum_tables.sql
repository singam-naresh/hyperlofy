-- V80: Create Enterprise Search Platform Enterprise Addendum Tables (Knowledge Graph, Conversational AI Search, Advanced RAG, & Search Governance)

-- 1. Knowledge Graph Nodes Table (Entities across all domains: Merchant, Product, Customer, Order, Case, Workflow)
CREATE TABLE IF NOT EXISTS knowledge_graph_nodes (
    id UUID PRIMARY KEY,
    entity_id VARCHAR(150) NOT NULL UNIQUE, -- Business entity ID
    entity_type VARCHAR(80) NOT NULL, -- MERCHANT, PRODUCT, CUSTOMER, ORDER, WORKFLOW, CASE, DOCUMENT
    entity_name VARCHAR(255) NOT NULL,
    metadata JSONB,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_kgn_type ON knowledge_graph_nodes(entity_type);
CREATE INDEX IF NOT EXISTS idx_kgn_tenant ON knowledge_graph_nodes(tenant_id);

-- 2. Knowledge Graph Edges Table (Semantic relationships between entities)
CREATE TABLE IF NOT EXISTS knowledge_graph_edges (
    id UUID PRIMARY KEY,
    source_node_id UUID NOT NULL REFERENCES knowledge_graph_nodes(id),
    target_node_id UUID NOT NULL REFERENCES knowledge_graph_nodes(id),
    relationship_type VARCHAR(80) NOT NULL, -- PURCHASED, VIEWED, BELONGS_TO, ASSIGNED_TO, CREATED_BY, APPROVED_BY, RELATED_TO
    weight NUMERIC(5,4) NOT NULL DEFAULT 1.0000,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_kge_source ON knowledge_graph_edges(source_node_id);
CREATE INDEX IF NOT EXISTS idx_kge_target ON knowledge_graph_edges(target_node_id);
CREATE INDEX IF NOT EXISTS idx_kge_rel ON knowledge_graph_edges(relationship_type);

-- 3. Search Conversations Table (Multi-turn Conversational AI Search with Citations)
CREATE TABLE IF NOT EXISTS search_conversations (
    id UUID PRIMARY KEY,
    conversation_code VARCHAR(100) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    user_query TEXT NOT NULL,
    ai_response TEXT NOT NULL,
    intent_type VARCHAR(80) NOT NULL DEFAULT 'SEARCH', -- SEARCH, QA, RETRIEVAL, SUMMARY
    confidence_score NUMERIC(5,2) NOT NULL DEFAULT 95.00,
    citation_sources TEXT, -- JSON or comma-separated source document IDs
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sc_code ON search_conversations(conversation_code);
CREATE INDEX IF NOT EXISTS idx_sc_user ON search_conversations(user_id);

-- 4. RAG Chunks Table (Advanced RAG Pipeline — Chunks, Embeddings, Reciprocal Rank Fusion)
CREATE TABLE IF NOT EXISTS rag_chunks (
    id UUID PRIMARY KEY,
    source_id VARCHAR(150) NOT NULL,
    source_type VARCHAR(80) NOT NULL, -- DOCUMENT, KNOWLEDGE_ARTICLE, WORKFLOW, CASE
    chunk_index INTEGER NOT NULL DEFAULT 0,
    chunk_text TEXT NOT NULL,
    vector_embedding TEXT, -- Serialized float vector
    relevance_score NUMERIC(5,4) NOT NULL DEFAULT 0.9000,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_rc_source ON rag_chunks(source_id);

-- 5. Search Governance Table (Sensitivity classification, audit, and retention)
CREATE TABLE IF NOT EXISTS search_governance (
    id UUID PRIMARY KEY,
    document_id VARCHAR(150) NOT NULL UNIQUE,
    sensitivity_level VARCHAR(30) NOT NULL DEFAULT 'INTERNAL', -- PUBLIC, INTERNAL, RESTRICTED, CONFIDENTIAL
    classification VARCHAR(80) NOT NULL DEFAULT 'STANDARD',
    owner_user_id UUID NOT NULL,
    access_roles VARCHAR(255) NOT NULL DEFAULT 'ROLE_USER',
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sg_doc ON search_governance(document_id);
