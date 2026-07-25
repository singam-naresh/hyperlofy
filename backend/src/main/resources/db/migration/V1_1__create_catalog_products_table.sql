-- Flyway migration: create catalog_products table
CREATE TABLE IF NOT EXISTS catalog_products (
    id UUID NOT NULL PRIMARY KEY,
    deleted boolean NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    created_by varchar(100) NOT NULL,
    updated_by varchar(100) NOT NULL,
    merchant_id UUID NOT NULL,
    sku varchar(120) NOT NULL,
    name varchar(200) NOT NULL,
    description text,
    category varchar(120),
    price numeric(12,2),
    unit varchar(50),
    available boolean NOT NULL DEFAULT true,
    stock_quantity int NOT NULL DEFAULT 0,
    image_url varchar(500)
);

CREATE INDEX IF NOT EXISTS idx_catalog_products_merchant ON catalog_products (merchant_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_catalog_products_sku ON catalog_products (sku);
