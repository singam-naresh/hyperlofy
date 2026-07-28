-- V87: Create Enterprise SEO, Discovery & Growth Platform Tables

-- 1. SEO Pages Table (Meta Titles, Meta Descriptions, Canonical URLs, Health Score)
CREATE TABLE IF NOT EXISTS seo_pages (
    id UUID PRIMARY KEY,
    page_url VARCHAR(500) NOT NULL UNIQUE,
    meta_title VARCHAR(255) NOT NULL,
    meta_description TEXT NOT NULL,
    canonical_url VARCHAR(500) NOT NULL,
    open_graph_title VARCHAR(255),
    open_graph_image VARCHAR(500),
    is_indexable BOOLEAN NOT NULL DEFAULT TRUE,
    seo_health_score NUMERIC(5,2) NOT NULL DEFAULT 95.00,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sp_url ON seo_pages(page_url);

-- 2. SEO Structured Data Table (Schema.org JSON-LD markup for Product, Merchant, FAQ)
CREATE TABLE IF NOT EXISTS seo_structured_data (
    id UUID PRIMARY KEY,
    page_id UUID NOT NULL REFERENCES seo_pages(id),
    schema_type VARCHAR(80) NOT NULL DEFAULT 'PRODUCT', -- PRODUCT, LOCAL_BUSINESS, BREADCRUMB, FAQ, REVIEW
    json_ld_content TEXT NOT NULL,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_ssd_page ON seo_structured_data(page_id);

-- 3. SEO Sitemaps Table (XML Sitemaps for Products, Merchants, Categories)
CREATE TABLE IF NOT EXISTS seo_sitemaps (
    id UUID PRIMARY KEY,
    sitemap_code VARCHAR(100) NOT NULL UNIQUE,
    sitemap_type VARCHAR(50) NOT NULL DEFAULT 'PRODUCT', -- PRODUCT, MERCHANT, CATEGORY, BRAND
    file_path VARCHAR(500) NOT NULL,
    total_urls INTEGER NOT NULL DEFAULT 10000,
    status VARCHAR(30) NOT NULL DEFAULT 'PUBLISHED', -- DRAFT, REGENERATING, PUBLISHED
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sm_code ON seo_sitemaps(sitemap_code);

-- 4. SEO Landing Pages Table (Auto-generated landing pages for Cities, Categories, Festivals)
CREATE TABLE IF NOT EXISTS seo_landing_pages (
    id UUID PRIMARY KEY,
    landing_code VARCHAR(100) NOT NULL UNIQUE,
    city_name VARCHAR(100) NOT NULL,
    category_name VARCHAR(100) NOT NULL,
    target_keyword VARCHAR(150) NOT NULL,
    page_path VARCHAR(255) NOT NULL UNIQUE,
    monthly_organic_views INTEGER NOT NULL DEFAULT 12500,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_slp_path ON seo_landing_pages(page_path);

-- 5. SEO Keyword Rankings Table (Search Engine Keyword Tracker & CTR Metrics)
CREATE TABLE IF NOT EXISTS seo_keyword_rankings (
    id UUID PRIMARY KEY,
    keyword VARCHAR(150) NOT NULL UNIQUE,
    current_rank INTEGER NOT NULL DEFAULT 1,
    previous_rank INTEGER NOT NULL DEFAULT 2,
    monthly_search_volume INTEGER NOT NULL DEFAULT 45000,
    click_through_rate NUMERIC(5,2) NOT NULL DEFAULT 18.50,
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_skr_kw ON seo_keyword_rankings(keyword);

-- 6. SEO Audit Reports Table (AI Content & Technical SEO Recommendations)
CREATE TABLE IF NOT EXISTS seo_audit_reports (
    id UUID PRIMARY KEY,
    audit_code VARCHAR(100) NOT NULL UNIQUE,
    page_id UUID NOT NULL REFERENCES seo_pages(id),
    health_score NUMERIC(5,2) NOT NULL DEFAULT 96.50,
    ai_recommendations TEXT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'COMPLETED',
    tenant_id UUID,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_sar_code ON seo_audit_reports(audit_code);
