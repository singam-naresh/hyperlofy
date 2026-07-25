-- Flyway migration: inventory tables
CREATE TABLE IF NOT EXISTS inventory_stock (
    id UUID NOT NULL PRIMARY KEY,
    merchant_id UUID NOT NULL,
    product_id UUID,
    sku varchar(120),
    available_quantity int NOT NULL DEFAULT 0,
    reserved_quantity int NOT NULL DEFAULT 0,
    low_stock_threshold int,
    available boolean NOT NULL DEFAULT true,
    updated_at timestamptz,
    version bigint
);

CREATE TABLE IF NOT EXISTS inventory_reservations (
    id UUID NOT NULL PRIMARY KEY,
    merchant_id UUID NOT NULL,
    product_id UUID,
    sku varchar(120),
    quantity int NOT NULL,
    status varchar(40),
    created_at timestamptz,
    updated_at timestamptz
);

CREATE INDEX IF NOT EXISTS idx_inventory_stock_merchant_product ON inventory_stock (merchant_id, product_id);
CREATE INDEX IF NOT EXISTS idx_inventory_stock_merchant_sku ON inventory_stock (merchant_id, sku);
CREATE INDEX IF NOT EXISTS idx_inventory_reservations_status ON inventory_reservations (status);
