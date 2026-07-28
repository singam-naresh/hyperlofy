-- V27: Create Marketplace Engine Tables (Phase 3)

-- 1. Brands Table
CREATE TABLE IF NOT EXISTS brands (
    id UUID PRIMARY KEY,
    brand_name VARCHAR(100) NOT NULL UNIQUE,
    logo_url VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 2. Marketplace Products Table
CREATE TABLE IF NOT EXISTS marketplace_products (
    id UUID PRIMARY KEY,
    merchant_id UUID NOT NULL,
    store_id UUID NOT NULL,
    category_id UUID NOT NULL,
    brand_id UUID,
    product_name VARCHAR(200) NOT NULL,
    short_description VARCHAR(500),
    long_description TEXT,
    sku VARCHAR(100) NOT NULL UNIQUE,
    barcode VARCHAR(100) UNIQUE,
    hsn_code VARCHAR(50),
    thumbnail_url VARCHAR(255),
    is_veg BOOLEAN DEFAULT TRUE,
    product_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, INACTIVE, HIDDEN, OUT_OF_STOCK
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_mkp_prod_store ON marketplace_products(store_id);
CREATE INDEX IF NOT EXISTS idx_mkp_prod_category ON marketplace_products(category_id);
CREATE INDEX IF NOT EXISTS idx_mkp_prod_status ON marketplace_products(product_status);

-- 3. Product Variants Table
CREATE TABLE IF NOT EXISTS product_variants (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    variant_name VARCHAR(100) NOT NULL, -- e.g., 250g, 500g, 1kg, 1L, Pack of 2
    sku VARCHAR(100) NOT NULL UNIQUE,
    barcode VARCHAR(100) UNIQUE,
    mrp DECIMAL(12, 2) NOT NULL,
    selling_price DECIMAL(12, 2) NOT NULL,
    offer_price DECIMAL(12, 2),
    discount_percentage DOUBLE PRECISION DEFAULT 0.0,
    weight_unit VARCHAR(20) NOT NULL DEFAULT 'g',
    variant_weight DOUBLE PRECISION NOT NULL DEFAULT 1.0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_variants_product ON product_variants(product_id);

-- 4. Inventory Tracking Table
CREATE TABLE IF NOT EXISTS inventory (
    id UUID PRIMARY KEY,
    variant_id UUID NOT NULL UNIQUE,
    available_stock INT NOT NULL DEFAULT 0,
    reserved_stock INT NOT NULL DEFAULT 0,
    sold_stock INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 5,
    auto_out_of_stock BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 5. Inventory Reservations Table
CREATE TABLE IF NOT EXISTS inventory_reservations (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    variant_id UUID NOT NULL,
    reserved_quantity INT NOT NULL,
    reservation_status VARCHAR(30) NOT NULL DEFAULT 'RESERVED', -- RESERVED, COMMITTED, EXPIRED, RELEASED
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_res_customer ON inventory_reservations(customer_id);
CREATE INDEX IF NOT EXISTS idx_res_variant ON inventory_reservations(variant_id);
