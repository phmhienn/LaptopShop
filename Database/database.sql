-- ================================================================
-- LaptopStoreDB - Complete Database Script
-- MySQL 8.0+
-- Compatible with MySQL Workbench, phpMyAdmin, HeidiSQL
-- ================================================================
-- Default password for all users: password
-- BCrypt hash: $2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa
-- ================================================================

SET FOREIGN_KEY_CHECKS = 0;
DROP DATABASE IF EXISTS LaptopStoreDB;
SET FOREIGN_KEY_CHECKS = 1;

CREATE DATABASE LaptopStoreDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE LaptopStoreDB;

-- ================================================================
-- TABLE: roles
-- ================================================================
CREATE TABLE roles (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255) NULL,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_roles_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: users
-- ================================================================
CREATE TABLE users (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    full_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20)  NULL,
    address    VARCHAR(500) NULL,
    avatar     VARCHAR(500) NULL,
    enabled    BOOLEAN      DEFAULT TRUE,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email    UNIQUE (email),
    INDEX idx_users_email    (email),
    INDEX idx_users_username (username),
    INDEX idx_users_enabled  (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: user_roles (join table)
-- ================================================================
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: addresses
-- ================================================================
CREATE TABLE addresses (
    id             BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT       NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    phone          VARCHAR(20)  NOT NULL,
    address_line   VARCHAR(255) NOT NULL,
    ward           VARCHAR(100) NULL,
    district       VARCHAR(100) NOT NULL,
    city           VARCHAR(100) NOT NULL,
    is_default     BOOLEAN      DEFAULT FALSE,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_addresses_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_addresses_user    (user_id),
    INDEX idx_addresses_default (user_id, is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: brands
-- ================================================================
CREATE TABLE brands (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL,
    logo        VARCHAR(500) NULL,
    description TEXT         NULL,
    status      BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_brands_name UNIQUE (name),
    CONSTRAINT uk_brands_slug UNIQUE (slug),
    INDEX idx_brands_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: categories
-- ================================================================
CREATE TABLE categories (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    slug        VARCHAR(100) NOT NULL,
    description TEXT         NULL,
    image       VARCHAR(500) NULL,
    parent_id   BIGINT       NULL,
    status      BOOLEAN      DEFAULT TRUE,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_categories_parent (parent_id),
    INDEX idx_categories_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: products
-- ================================================================
CREATE TABLE products (
    id             BIGINT          AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(255)    NOT NULL,
    slug           VARCHAR(255)    NOT NULL,
    description    TEXT            NULL,
    cpu            VARCHAR(100)    NULL,
    ram            VARCHAR(50)     NULL,
    ssd            VARCHAR(50)     NULL,
    gpu            VARCHAR(100)    NULL,
    display        VARCHAR(100)    NULL,
    battery        VARCHAR(50)     NULL,
    weight         DECIMAL(5,2)    NULL,
    warranty       INT             NULL COMMENT 'Warranty in months',
    price          DECIMAL(12,2)   NOT NULL,
    discount_price DECIMAL(12,2)   NULL,
    stock          INT             DEFAULT 0,
    thumbnail      VARCHAR(500)    NULL,
    brand_id       BIGINT          NULL,
    category_id    BIGINT          NULL,
    status         ENUM('ACTIVE','INACTIVE','DISCONTINUED') DEFAULT 'ACTIVE',
    featured       BOOLEAN         DEFAULT FALSE,
    created_at     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_products_slug UNIQUE (slug),
    CONSTRAINT fk_products_brand    FOREIGN KEY (brand_id)    REFERENCES brands(id)     ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_products_price          CHECK (price > 0),
    CONSTRAINT chk_products_discount_price CHECK (discount_price IS NULL OR discount_price > 0),
    CONSTRAINT chk_products_stock          CHECK (stock >= 0),
    INDEX idx_products_brand    (brand_id),
    INDEX idx_products_category (category_id),
    INDEX idx_products_status   (status),
    INDEX idx_products_featured (featured),
    INDEX idx_products_price    (price),
    INDEX idx_products_name     (name),
    INDEX idx_products_brand_category (brand_id, category_id),
    INDEX idx_products_status_featured (status, featured)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: product_images
-- ================================================================
CREATE TABLE product_images (
    id            BIGINT       AUTO_INCREMENT PRIMARY KEY,
    product_id    BIGINT       NOT NULL,
    image_url     VARCHAR(500) NOT NULL,
    display_order INT          DEFAULT 0,
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_product_images_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: wishlists
-- ================================================================
CREATE TABLE wishlists (
    id         BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    name       VARCHAR(100) DEFAULT 'My Wishlist',
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_wishlists_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_wishlists_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: wishlist_items
-- ================================================================
CREATE TABLE wishlist_items (
    id          BIGINT    AUTO_INCREMENT PRIMARY KEY,
    wishlist_id BIGINT    NOT NULL,
    product_id  BIGINT    NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_wishlist_items_wishlist FOREIGN KEY (wishlist_id) REFERENCES wishlists(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_items_product  FOREIGN KEY (product_id)  REFERENCES products(id)  ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_wishlist_items UNIQUE (wishlist_id, product_id),
    INDEX idx_wishlist_items_wishlist (wishlist_id),
    INDEX idx_wishlist_items_product  (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: carts
-- ================================================================
CREATE TABLE carts (
    id         BIGINT    AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_carts_user UNIQUE (user_id),
    CONSTRAINT fk_carts_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: cart_items
-- ================================================================
CREATE TABLE cart_items (
    id         BIGINT    AUTO_INCREMENT PRIMARY KEY,
    cart_id    BIGINT    NOT NULL,
    product_id BIGINT    NOT NULL,
    quantity   INT       NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_items_cart    FOREIGN KEY (cart_id)    REFERENCES carts(id)    ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_cart_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_cart_items UNIQUE (cart_id, product_id),
    CONSTRAINT chk_cart_items_quantity CHECK (quantity > 0),
    INDEX idx_cart_items_cart    (cart_id),
    INDEX idx_cart_items_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: coupons
-- ================================================================
CREATE TABLE coupons (
    id                 BIGINT         AUTO_INCREMENT PRIMARY KEY,
    code               VARCHAR(50)    NOT NULL,
    description        VARCHAR(255)   NULL,
    discount_type      ENUM('PERCENTAGE','FIXED') NOT NULL,
    discount_value     DECIMAL(12,2)  NOT NULL,
    min_order_amount   DECIMAL(12,2)  DEFAULT 0,
    max_discount_amount DECIMAL(12,2) NULL,
    usage_limit        INT            DEFAULT 0 COMMENT '0 = unlimited',
    used_count         INT            DEFAULT 0,
    start_date         DATETIME       NOT NULL,
    end_date           DATETIME       NOT NULL,
    status             ENUM('ACTIVE','INACTIVE','EXPIRED') DEFAULT 'ACTIVE',
    created_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at         TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_coupons_code UNIQUE (code),
    CONSTRAINT chk_coupons_discount_value CHECK (discount_value > 0),
    CONSTRAINT chk_coupons_dates CHECK (end_date > start_date),
    INDEX idx_coupons_code   (code),
    INDEX idx_coupons_status (status),
    INDEX idx_coupons_dates  (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: orders
-- ================================================================
CREATE TABLE orders (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id          BIGINT         NOT NULL,
    order_code       VARCHAR(50)    NOT NULL,
    total_amount     DECIMAL(12,2)  NOT NULL,
    discount_amount  DECIMAL(12,2)  DEFAULT 0,
    final_amount     DECIMAL(12,2)  NOT NULL,
    note             TEXT           NULL,
    coupon_id        BIGINT         NULL,
    created_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_orders_code UNIQUE (order_code),
    CONSTRAINT fk_orders_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_orders_coupon FOREIGN KEY (coupon_id) REFERENCES coupons(id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT chk_orders_total    CHECK (total_amount >= 0),
    CONSTRAINT chk_orders_discount CHECK (discount_amount >= 0),
    CONSTRAINT chk_orders_final    CHECK (final_amount >= 0),
    INDEX idx_orders_user   (user_id),
    INDEX idx_orders_code   (order_code),
    INDEX idx_orders_date   (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: order_items
-- ================================================================
CREATE TABLE order_items (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT        NOT NULL,
    product_id    BIGINT        NOT NULL,
    product_name  VARCHAR(255)  NOT NULL,
    product_price DECIMAL(12,2) NOT NULL,
    quantity      INT           NOT NULL,
    subtotal      DECIMAL(12,2) NOT NULL,
    created_at    TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_items_order   FOREIGN KEY (order_id)   REFERENCES orders(id)   ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_order_items_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_items_subtotal CHECK (subtotal >= 0),
    INDEX idx_order_items_order   (order_id),
    INDEX idx_order_items_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: payments
-- ================================================================
CREATE TABLE payments (
    id             BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id       BIGINT        NOT NULL,
    method         VARCHAR(50)   NOT NULL DEFAULT 'COD',
    amount         DECIMAL(12,2) NOT NULL,
    status         ENUM('UNPAID','PAID','FAILED','REFUNDED') DEFAULT 'UNPAID',
    transaction_id VARCHAR(100)  NULL,
    paid_at        DATETIME      NULL,
    created_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_payments_amount CHECK (amount >= 0),
    INDEX idx_payments_order  (order_id),
    INDEX idx_payments_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: shipments
-- ================================================================
CREATE TABLE shipments (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT       NOT NULL,
    receiver_name   VARCHAR(100) NOT NULL,
    receiver_phone  VARCHAR(20)  NOT NULL,
    receiver_address TEXT        NOT NULL,
    tracking_number VARCHAR(100) NULL,
    shipping_provider VARCHAR(100) NULL,
    shipping_fee    DECIMAL(12,2) DEFAULT 0,
    status          ENUM('PENDING','READY_TO_SHIP','SHIPPING','DELIVERED','FAILED','RETURNED') DEFAULT 'PENDING',
    shipped_at      DATETIME     NULL,
    delivered_at    DATETIME     NULL,
    created_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_shipments_order UNIQUE (order_id),
    CONSTRAINT fk_shipments_order FOREIGN KEY (order_id) REFERENCES orders(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_shipments_order  (order_id),
    INDEX idx_shipments_status (status),
    INDEX idx_shipments_tracking (tracking_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: reviews
-- ================================================================
CREATE TABLE reviews (
    id         BIGINT    AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT    NOT NULL,
    product_id BIGINT    NOT NULL,
    rating     INT       NOT NULL,
    comment    TEXT      NULL,
    status     ENUM('PENDING','APPROVED','REJECTED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_reviews_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT uk_reviews_user_product UNIQUE (user_id, product_id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    INDEX idx_reviews_user    (user_id),
    INDEX idx_reviews_product (product_id),
    INDEX idx_reviews_rating  (rating),
    INDEX idx_reviews_status  (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: notifications
-- ================================================================
CREATE TABLE notifications (
    id           BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    message      TEXT         NOT NULL,
    type         ENUM('ORDER','PAYMENT','PROMOTION','SYSTEM') NOT NULL,
    is_read      BOOLEAN      DEFAULT FALSE,
    reference_id VARCHAR(100) NULL,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_notifications_user    (user_id),
    INDEX idx_notifications_type    (type),
    INDEX idx_notifications_read    (user_id, is_read),
    INDEX idx_notifications_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: inventory
-- ================================================================
CREATE TABLE inventory (
    id                BIGINT    AUTO_INCREMENT PRIMARY KEY,
    product_id        BIGINT    NOT NULL,
    quantity          INT       NOT NULL DEFAULT 0,
    reserved_quantity INT       NOT NULL DEFAULT 0,
    last_restocked_at DATETIME  NULL,
    created_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_inventory_product UNIQUE (product_id),
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_inventory_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_inventory_reserved CHECK (reserved_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: flash_sales
-- ================================================================
CREATE TABLE flash_sales (
    id         BIGINT        AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT        NOT NULL,
    sale_price DECIMAL(12,2) NOT NULL,
    stock      INT           NOT NULL DEFAULT 0,
    sold       INT           NOT NULL DEFAULT 0,
    start_time DATETIME      NOT NULL,
    end_time   DATETIME      NOT NULL,
    status     ENUM('UPCOMING','ACTIVE','ENDED') DEFAULT 'UPCOMING',
    created_at TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_flash_sales_product FOREIGN KEY (product_id) REFERENCES products(id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT chk_flash_sales_price CHECK (sale_price > 0),
    CONSTRAINT chk_flash_sales_stock CHECK (stock >= 0),
    CONSTRAINT chk_flash_sales_sold  CHECK (sold >= 0),
    CONSTRAINT chk_flash_sales_dates CHECK (end_time > start_time),
    INDEX idx_flash_sales_product (product_id),
    INDEX idx_flash_sales_status  (status),
    INDEX idx_flash_sales_dates   (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: refresh_tokens
-- ================================================================
CREATE TABLE refresh_tokens (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(500) NOT NULL,
    expiry_date DATETIME     NOT NULL,
    revoked     BOOLEAN      DEFAULT FALSE,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user  (user_id),
    INDEX idx_refresh_tokens_token (token),
    INDEX idx_refresh_tokens_expiry (expiry_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- TABLE: audit_logs
-- ================================================================
CREATE TABLE audit_logs (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT       NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id   BIGINT       NULL,
    old_value   TEXT         NULL,
    new_value   TEXT         NULL,
    ip_address  VARCHAR(50)  NULL,
    created_at  TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON UPDATE CASCADE ON DELETE SET NULL,
    INDEX idx_audit_logs_user   (user_id),
    INDEX idx_audit_logs_action (action),
    INDEX idx_audit_logs_entity (entity_type, entity_id),
    INDEX idx_audit_logs_date   (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ================================================================
-- SAMPLE DATA
-- ================================================================

-- ----------------------------------------------------------------
-- Roles
-- ----------------------------------------------------------------
INSERT INTO roles (id, name, description) VALUES
(1, 'ADMIN', 'System Administrator with full access'),
(2, 'USER',  'Regular user with standard access');

-- ----------------------------------------------------------------
-- Users (1 Admin + 10 Users)
-- Password for all: password
-- BCrypt hash generated with cost factor 10
-- ----------------------------------------------------------------
INSERT INTO users (id, username, email, password, full_name, phone, avatar, enabled) VALUES
(1,  'admin',   'admin@laptopstore.com',   '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'System Admin',    '0901000000', NULL, TRUE),
(2,  'user01',  'user01@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Nguyen Van An',   '0901000001', NULL, TRUE),
(3,  'user02',  'user02@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Tran Thi Bich',   '0901000002', NULL, TRUE),
(4,  'user03',  'user03@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Le Van Cuong',    '0901000003', NULL, TRUE),
(5,  'user04',  'user04@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Pham Thi Dung',   '0901000004', NULL, TRUE),
(6,  'user05',  'user05@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Hoang Van Em',    '0901000005', NULL, TRUE),
(7,  'user06',  'user06@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Vo Thi Fang',     '0901000006', NULL, TRUE),
(8,  'user07',  'user07@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Dang Van Giang',  '0901000007', NULL, TRUE),
(9,  'user08',  'user08@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Bui Thi Hoa',     '0901000008', NULL, TRUE),
(10, 'user09',  'user09@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Ngo Van Ich',     '0901000009', NULL, TRUE),
(11, 'user10',  'user10@gmail.com',        '$2a$10$dXJ3SW6G7P50lGmMQgel5u/sLkFp3l.qpG3GOuPQyXNqLGaAbVFLa', 'Ly Thi Kim',      '0901000010', NULL, TRUE);

-- ----------------------------------------------------------------
-- User Roles
-- ----------------------------------------------------------------
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), (1, 2),
(2, 2), (3, 2), (4, 2), (5, 2), (6, 2),
(7, 2), (8, 2), (9, 2), (10, 2), (11, 2);

-- ----------------------------------------------------------------
-- Addresses
-- ----------------------------------------------------------------
INSERT INTO addresses (id, user_id, recipient_name, phone, address_line, ward, district, city, is_default) VALUES
(1,  1,  'System Admin',    '0901000000', '1 Nguyen Hue',       'Ben Nghe',      'Quan 1',      'Ho Chi Minh',  TRUE),
(2,  2,  'Nguyen Van An',   '0901000001', '123 Le Loi',         'Ben Thanh',     'Quan 1',      'Ho Chi Minh',  TRUE),
(3,  2,  'Nguyen Van An',   '0901000001', '456 Tran Hung Dao',  'Pham Ngu Lao',  'Quan 1',      'Ho Chi Minh',  FALSE),
(4,  3,  'Tran Thi Bich',   '0901000002', '789 Hai Ba Trung',   'Tan Dinh',      'Quan 1',      'Ho Chi Minh',  TRUE),
(5,  4,  'Le Van Cuong',    '0901000003', '12 Phan Chu Trinh',  'Hoan Kiem',     'Hoan Kiem',   'Ha Noi',       TRUE),
(6,  5,  'Pham Thi Dung',   '0901000004', '34 Kim Ma',          'Giang Vo',      'Ba Dinh',     'Ha Noi',       TRUE),
(7,  6,  'Hoang Van Em',    '0901000005', '56 Bach Dang',       'Hai Chau 1',    'Hai Chau',    'Da Nang',      TRUE),
(8,  7,  'Vo Thi Fang',     '0901000006', '78 Nguyen Trai',     'Phu Hoi',       'Hue',         'Thua Thien Hue', TRUE),
(9,  8,  'Dang Van Giang',  '0901000007', '90 Le Duan',         'Thach Thang',   'Hai Chau',    'Da Nang',      TRUE),
(10, 9,  'Bui Thi Hoa',     '0901000008', '111 Vo Van Kiet',    'Cau Ong Lanh',  'Quan 1',      'Ho Chi Minh',  TRUE),
(11, 10, 'Ngo Van Ich',     '0901000009', '222 Nguyen Van Linh','An Khanh',      'Ninh Kieu',   'Can Tho',      TRUE),
(12, 11, 'Ly Thi Kim',      '0901000010', '333 Tran Phu',       'Loc Tho',       'Nha Trang',   'Khanh Hoa',    TRUE);

-- ----------------------------------------------------------------
-- Brands (10 laptop brands)
-- ----------------------------------------------------------------
INSERT INTO brands (id, name, slug, logo, description, status) VALUES
(1,  'Apple',    'apple',    '/images/brands/apple.png',    'Apple Inc. - Think Different',                              TRUE),
(2,  'Dell',     'dell',     '/images/brands/dell.png',     'Dell Technologies - Premium computing solutions',            TRUE),
(3,  'HP',       'hp',       '/images/brands/hp.png',       'Hewlett-Packard - Innovation that matters',                  TRUE),
(4,  'Lenovo',   'lenovo',   '/images/brands/lenovo.png',   'Lenovo - Smarter Technology for All',                        TRUE),
(5,  'Asus',     'asus',     '/images/brands/asus.png',     'ASUS - In Search of Incredible',                             TRUE),
(6,  'Acer',     'acer',     '/images/brands/acer.png',     'Acer - Explore Beyond Limits',                               TRUE),
(7,  'MSI',      'msi',      '/images/brands/msi.png',      'Micro-Star International - True Gaming',                     TRUE),
(8,  'Gigabyte', 'gigabyte', '/images/brands/gigabyte.png', 'GIGABYTE - Upgrade Your Life',                               TRUE),
(9,  'Huawei',   'huawei',   '/images/brands/huawei.png',   'Huawei - Building a Fully Connected Intelligent World',     TRUE),
(10, 'Razer',    'razer',    '/images/brands/razer.png',    'Razer - For Gamers. By Gamers.',                              TRUE);

-- ----------------------------------------------------------------
-- Categories (10 laptop categories)
-- ----------------------------------------------------------------
INSERT INTO categories (id, name, slug, description, image, parent_id, status) VALUES
(1,  'Gaming Laptop',      'gaming-laptop',      'High-performance laptops for gaming enthusiasts',          '/images/categories/gaming.png',      NULL, TRUE),
(2,  'Ultrabook',           'ultrabook',           'Thin, lightweight, premium ultraportable laptops',         '/images/categories/ultrabook.png',    NULL, TRUE),
(3,  'Business Laptop',     'business-laptop',     'Professional laptops for enterprise and business',         '/images/categories/business.png',     NULL, TRUE),
(4,  'Workstation',         'workstation',         'Mobile workstations for demanding professional tasks',     '/images/categories/workstation.png',  NULL, TRUE),
(5,  '2-in-1 Convertible',  '2-in-1-convertible',  'Versatile laptops with tablet mode capability',            '/images/categories/convertible.png',  NULL, TRUE),
(6,  'Student Laptop',      'student-laptop',      'Affordable laptops perfect for students and education',    '/images/categories/student.png',      NULL, TRUE),
(7,  'Content Creation',    'content-creation',    'Laptops optimized for video editing and creative work',    '/images/categories/creation.png',     NULL, TRUE),
(8,  'Budget Laptop',       'budget-laptop',       'Affordable laptops with solid everyday performance',       '/images/categories/budget.png',       NULL, TRUE),
(9,  'Premium Laptop',      'premium-laptop',      'High-end laptops with premium build and features',         '/images/categories/premium.png',      NULL, TRUE),
(10, 'Thin and Light',      'thin-and-light',      'Ultra-portable laptops prioritizing portability',          '/images/categories/thin-light.png',   NULL, TRUE);

-- ----------------------------------------------------------------
-- Products (50 laptops)
-- ----------------------------------------------------------------
INSERT INTO products (id, name, slug, description, cpu, ram, ssd, gpu, display, battery, weight, warranty, price, discount_price, stock, thumbnail, brand_id, category_id, status, featured) VALUES
-- Apple (brand_id=1)
(1,  'MacBook Air M3 13 inch',       'macbook-air-m3-13',       'The incredibly thin MacBook Air with the powerful M3 chip delivers faster performance in an ultraportable design.',                                 'Apple M3 8-core',          '8GB Unified',   '256GB', 'Apple 10-core GPU',       '13.6 inch Liquid Retina',    '18 hours',  1.24, 12, 1099.00, NULL,    50, '/images/products/macbook-air-m3-13.png',       1, 2,  'ACTIVE', TRUE),
(2,  'MacBook Air M3 15 inch',       'macbook-air-m3-15',       'The 15-inch MacBook Air with M3 chip offers a spacious display in an impossibly thin design.',                                                      'Apple M3 8-core',          '8GB Unified',   '256GB', 'Apple 10-core GPU',       '15.3 inch Liquid Retina',    '18 hours',  1.51, 12, 1299.00, 1199.00, 40, '/images/products/macbook-air-m3-15.png',       1, 10, 'ACTIVE', TRUE),
(3,  'MacBook Pro 14 inch M3',       'macbook-pro-14-m3',       'MacBook Pro with M3 chip delivers exceptional performance for demanding workflows with stunning XDR display.',                                      'Apple M3 8-core',          '8GB Unified',   '512GB', 'Apple 10-core GPU',       '14.2 inch Liquid Retina XDR','17 hours',  1.55, 12, 1599.00, NULL,    35, '/images/products/macbook-pro-14-m3.png',       1, 9,  'ACTIVE', TRUE),
(4,  'MacBook Pro 16 inch M3 Pro',   'macbook-pro-16-m3-pro',   'The ultimate pro laptop with M3 Pro chip, featuring up to 22 hours of battery life and a stunning 16-inch display.',                                'Apple M3 Pro 12-core',     '18GB Unified',  '512GB', 'Apple 18-core GPU',       '16.2 inch Liquid Retina XDR','22 hours',  2.14, 12, 2499.00, NULL,    25, '/images/products/macbook-pro-16-m3-pro.png',   1, 7,  'ACTIVE', TRUE),
(5,  'MacBook Pro 14 inch M3 Max',   'macbook-pro-14-m3-max',   'Maximum performance in a pro laptop. M3 Max delivers extraordinary power for the most demanding professional workflows.',                            'Apple M3 Max 16-core',     '36GB Unified',  '1TB',   'Apple 40-core GPU',       '14.2 inch Liquid Retina XDR','17 hours',  1.61, 12, 3499.00, 3299.00, 15, '/images/products/macbook-pro-14-m3-max.png',   1, 4,  'ACTIVE', TRUE),

-- Dell (brand_id=2)
(6,  'Dell XPS 13 Plus',             'dell-xps-13-plus',        'A strikingly beautiful laptop with edge-to-edge keyboard, seamless glass touchpad, and InfinityEdge display.',                                       'Intel Core Ultra 7 155H',  '16GB LPDDR5x', '512GB', 'Intel Arc Graphics',      '13.4 inch FHD+ OLED',       '13 hours',  1.23, 12, 1299.00, NULL,    30, '/images/products/dell-xps-13-plus.png',        2, 2,  'ACTIVE', TRUE),
(7,  'Dell XPS 15',                  'dell-xps-15',             'A powerful 15-inch laptop with stunning OLED display, premium build quality, and creative-class performance.',                                        'Intel Core i7-13700H',     '16GB DDR5',     '512GB', 'NVIDIA RTX 4050 6GB',     '15.6 inch 3.5K OLED',       '13 hours',  1.86, 12, 1699.00, NULL,    25, '/images/products/dell-xps-15.png',             2, 9,  'ACTIVE', FALSE),
(8,  'Dell XPS 17',                  'dell-xps-17',             'The largest XPS laptop with a stunning 4K display, powerful internals, and uncompromising portability.',                                              'Intel Core i7-13700H',     '32GB DDR5',     '1TB',   'NVIDIA RTX 4070 8GB',     '17 inch 4K UHD+',           '11 hours',  2.44, 12, 2499.00, NULL,    20, '/images/products/dell-xps-17.png',             2, 7,  'ACTIVE', FALSE),
(9,  'Dell Inspiron 16',             'dell-inspiron-16',        'A versatile 16-inch laptop for everyday productivity with a comfortable keyboard and ample screen space.',                                            'Intel Core i5-1335U',      '8GB DDR4',      '512GB', 'Intel Iris Xe',           '16 inch FHD+',               '10 hours',  1.87, 12, 649.00,  599.00,  60, '/images/products/dell-inspiron-16.png',         2, 6,  'ACTIVE', FALSE),
(10, 'Dell Latitude 14',             'dell-latitude-14',        'Enterprise-grade business laptop with robust security features, manageability, and all-day battery life.',                                            'Intel Core i7-1365U',      '16GB DDR5',     '512GB', 'Intel Iris Xe',           '14 inch FHD',                '11 hours',  1.53, 36, 1399.00, 1299.00, 30, '/images/products/dell-latitude-14.png',         2, 3,  'ACTIVE', FALSE),
(11, 'Dell Precision 5680',          'dell-precision-5680',     'Professional mobile workstation with ISV-certified graphics and enterprise reliability for demanding applications.',                                   'Intel Core i9-13900H',     '32GB DDR5',     '1TB',   'NVIDIA RTX 3500 Ada 12GB','16 inch 4K OLED',            '10 hours',  2.06, 36, 3299.00, NULL,    15, '/images/products/dell-precision-5680.png',     2, 4,  'ACTIVE', FALSE),

-- HP (brand_id=3)
(12, 'HP Spectre x360 14',           'hp-spectre-x360-14',      'A stunning 2-in-1 convertible with OLED display, premium design, and versatile form factors for any workflow.',                                       'Intel Core Ultra 7 155H',  '16GB LPDDR5x', '1TB',   'Intel Arc Graphics',      '14 inch 2.8K OLED',          '15 hours',  1.34, 12, 1599.00, NULL,    25, '/images/products/hp-spectre-x360-14.png',      3, 5,  'ACTIVE', TRUE),
(13, 'HP Envy 16',                   'hp-envy-16',              'Creative powerhouse with brilliant OLED display and dedicated graphics for content creation and entertainment.',                                       'Intel Core i7-13700H',     '16GB DDR5',     '512GB', 'NVIDIA RTX 4060 8GB',     '16 inch 4K OLED',            '11 hours',  2.19, 12, 1499.00, 1399.00, 20, '/images/products/hp-envy-16.png',              3, 7,  'ACTIVE', FALSE),
(14, 'HP Pavilion 15',               'hp-pavilion-15',          'Reliable everyday laptop with modern design, solid performance, and an affordable price point.',                                                       'Intel Core i5-1335U',      '8GB DDR4',      '256GB', 'Intel Iris Xe',           '15.6 inch FHD',              '8 hours',   1.75, 12, 549.00,  499.00,  70, '/images/products/hp-pavilion-15.png',          3, 6,  'ACTIVE', FALSE),
(15, 'HP EliteBook 840 G10',         'hp-elitebook-840-g10',    'Premium business laptop with top-tier security, enterprise manageability, and collaboration features.',                                                'Intel Core i7-1365U',      '16GB DDR5',     '512GB', 'Intel Iris Xe',           '14 inch WUXGA',              '14 hours',  1.36, 36, 1599.00, NULL,    20, '/images/products/hp-elitebook-840-g10.png',    3, 3,  'ACTIVE', FALSE),
(16, 'HP ZBook Studio G10',          'hp-zbook-studio-g10',     'Ultimate mobile workstation with DreamColor display and professional-grade performance for creative workflows.',                                       'Intel Core i9-13900H',     '32GB DDR5',     '1TB',   'NVIDIA RTX 4080 12GB',    '16 inch 4K DreamColor',      '10 hours',  1.73, 36, 3899.00, NULL,    10, '/images/products/hp-zbook-studio-g10.png',     3, 4,  'ACTIVE', FALSE),

-- Lenovo (brand_id=4)
(17, 'Lenovo ThinkPad X1 Carbon Gen 11', 'thinkpad-x1-carbon-gen11', 'The iconic business ultrabook with legendary ThinkPad reliability, stunning OLED display, and all-day battery.',                                  'Intel Core i7-1365U',      '16GB LPDDR5',   '512GB', 'Intel Iris Xe',           '14 inch 2.8K OLED',          '15 hours',  1.12, 36, 1749.00, NULL,    25, '/images/products/thinkpad-x1-carbon-gen11.png',4, 3,  'ACTIVE', TRUE),
(18, 'Lenovo ThinkPad T14s Gen 4',       'thinkpad-t14s-gen4',       'Versatile business laptop with military-grade durability, fast charging, and enterprise security features.',                                        'Intel Core i7-1365U',      '16GB LPDDR5',   '512GB', 'Intel Iris Xe',           '14 inch WUXGA',              '13 hours',  1.22, 36, 1499.00, NULL,    30, '/images/products/thinkpad-t14s-gen4.png',      4, 3,  'ACTIVE', FALSE),
(19, 'Lenovo IdeaPad 5 Pro',             'ideapad-5-pro',            'Feature-packed laptop for students and professionals with a vibrant 2.5K display and strong all-around performance.',                               'AMD Ryzen 7 7735U',        '16GB DDR5',     '512GB', 'AMD Radeon 680M',         '16 inch 2.5K IPS',           '12 hours',  1.89, 24, 799.00,  NULL,    45, '/images/products/ideapad-5-pro.png',           4, 6,  'ACTIVE', FALSE),
(20, 'Lenovo Legion Pro 5 16',           'legion-pro-5-16',          'Powerhouse gaming laptop with blazing-fast display, advanced cooling, and desktop-class performance.',                                               'AMD Ryzen 9 7945HX',       '32GB DDR5',     '1TB',   'NVIDIA RTX 4070 8GB',     '16 inch WQXGA 240Hz',        '8 hours',   2.49, 24, 1899.00, 1799.00, 20, '/images/products/legion-pro-5-16.png',         4, 1,  'ACTIVE', TRUE),
(21, 'Lenovo Yoga 9i 14',                'yoga-9i-14',               'Premium 2-in-1 convertible with rotating soundbar, gorgeous OLED display, and refined design.',                                                     'Intel Core i7-1360P',      '16GB LPDDR5',   '1TB',   'Intel Iris Xe',           '14 inch 4K OLED',            '14 hours',  1.37, 12, 1599.00, NULL,    20, '/images/products/yoga-9i-14.png',              4, 5,  'ACTIVE', FALSE),
(22, 'Lenovo ThinkPad P16s Gen 2',       'thinkpad-p16s-gen2',       'Portable workstation with ISV-certified graphics, ThinkPad reliability, and a large 16-inch display.',                                              'Intel Core i7-1370P',      '32GB DDR5',     '1TB',   'NVIDIA RTX A500 4GB',     '16 inch WQXGA',              '12 hours',  1.97, 36, 1899.00, NULL,    15, '/images/products/thinkpad-p16s-gen2.png',      4, 4,  'ACTIVE', FALSE),

-- Asus (brand_id=5)
(23, 'ASUS ROG Strix G16',               'rog-strix-g16',            'Dominate the competition with a blazing-fast 240Hz display, powerful Intel processor, and advanced cooling technology.',                              'Intel Core i9-13980HX',    '16GB DDR5',     '1TB',   'NVIDIA RTX 4070 8GB',     '16 inch QHD 240Hz',          '6 hours',   2.50, 24, 1799.00, NULL,    25, '/images/products/rog-strix-g16.png',           5, 1,  'ACTIVE', TRUE),
(24, 'ASUS ZenBook 14 OLED',             'zenbook-14-oled',          'Stunning OLED ultrabook with an incredibly slim design, vibrant display, and long battery life for professionals on the go.',                        'Intel Core Ultra 7 155H',  '16GB LPDDR5x', '512GB', 'Intel Arc Graphics',      '14 inch 2.8K OLED',          '15 hours',  1.20, 24, 1199.00, NULL,    35, '/images/products/zenbook-14-oled.png',         5, 2,  'ACTIVE', TRUE),
(25, 'ASUS VivoBook 15',                 'vivobook-15',              'Affordable everyday laptop with a clean design, reliable performance, and essential features for daily tasks.',                                       'Intel Core i5-1235U',      '8GB DDR4',      '256GB', 'Intel Iris Xe',           '15.6 inch FHD',              '8 hours',   1.70, 24, 449.00,  399.00,  80, '/images/products/vivobook-15.png',             5, 8,  'ACTIVE', FALSE),
(26, 'ASUS TUF Gaming A15',              'tuf-gaming-a15',           'Military-grade tough gaming laptop with powerful specs, high-refresh display, and MUX Switch for optimal gaming.',                                    'AMD Ryzen 7 7735HS',       '16GB DDR5',     '512GB', 'NVIDIA RTX 4060 8GB',     '15.6 inch FHD 144Hz',        '7 hours',   2.20, 24, 1099.00, 999.00,  40, '/images/products/tuf-gaming-a15.png',          5, 1,  'ACTIVE', FALSE),
(27, 'ASUS ProArt Studiobook 16 OLED',   'proart-studiobook-16',     'Professional-grade laptop for creators with ASUS Dial, stunning OLED display, and ISV-certified performance.',                                       'Intel Core i9-13980HX',    '32GB DDR5',     '1TB',   'NVIDIA RTX 4070 8GB',     '16 inch 4K OLED',            '8 hours',   2.40, 24, 2499.00, NULL,    15, '/images/products/proart-studiobook-16.png',    5, 7,  'ACTIVE', FALSE),
(28, 'ASUS ROG Zephyrus G14',            'rog-zephyrus-g14',         'Ultra-slim gaming powerhouse that defies convention with incredible performance in a compact 14-inch form factor.',                                   'AMD Ryzen 9 7940HS',       '16GB LPDDR5',   '1TB',   'NVIDIA RTX 4060 8GB',     '14 inch QHD 165Hz',          '10 hours',  1.65, 24, 1599.00, NULL,    20, '/images/products/rog-zephyrus-g14.png',        5, 1,  'ACTIVE', TRUE),

-- Acer (brand_id=6)
(29, 'Acer Predator Helios 16',          'predator-helios-16',       'Beast-mode gaming laptop with top-tier cooling, ultra-fast display, and raw power for AAA gaming excellence.',                                        'Intel Core i9-13900HX',    '16GB DDR5',     '1TB',   'NVIDIA RTX 4080 12GB',    '16 inch WQXGA 240Hz',        '6 hours',   2.60, 24, 2399.00, NULL,    18, '/images/products/predator-helios-16.png',      6, 1,  'ACTIVE', TRUE),
(30, 'Acer Swift Go 14',                 'swift-go-14',              'Ultra-portable laptop with OLED brilliance, AI-powered features, and all-day battery life in a lightweight chassis.',                                 'Intel Core Ultra 7 155H',  '16GB LPDDR5x', '512GB', 'Intel Arc Graphics',      '14 inch 2.8K OLED',          '12 hours',  1.25, 24, 999.00,  899.00,  35, '/images/products/swift-go-14.png',             6, 10, 'ACTIVE', FALSE),
(31, 'Acer Aspire 5',                    'acer-aspire-5',            'Well-rounded budget laptop offering solid performance, decent display, and reliable build for everyday computing.',                                   'Intel Core i5-1335U',      '8GB DDR4',      '256GB', 'Intel Iris Xe',           '15.6 inch FHD',              '9 hours',   1.76, 12, 499.00,  NULL,    65, '/images/products/acer-aspire-5.png',           6, 8,  'ACTIVE', FALSE),
(32, 'Acer Nitro V 15',                  'acer-nitro-v-15',          'Entry-level gaming laptop that delivers smooth gameplay with dedicated graphics at an accessible price.',                                              'AMD Ryzen 5 7535HS',       '8GB DDR5',      '512GB', 'NVIDIA RTX 4050 6GB',     '15.6 inch FHD 144Hz',        '7 hours',   2.10, 12, 799.00,  NULL,    45, '/images/products/acer-nitro-v-15.png',         6, 1,  'ACTIVE', FALSE),
(33, 'Acer ConceptD 7',                  'acer-conceptd-7',          'Professional-grade creator laptop with Pantone-validated 4K display, powerful GPU, and whisper-quiet operation.',                                      'Intel Core i7-12700H',     '32GB DDR5',     '1TB',   'NVIDIA RTX 3080 Ti 16GB', '15.6 inch 4K UHD',           '8 hours',   2.45, 36, 2999.00, NULL,    10, '/images/products/acer-conceptd-7.png',         6, 7,  'ACTIVE', FALSE),

-- MSI (brand_id=7)
(34, 'MSI Raider GE78 HX',               'msi-raider-ge78-hx',       'Flagship gaming laptop with desktop-rivaling performance, massive 17-inch display, and enthusiast-grade specs.',                                     'Intel Core i9-13980HX',    '32GB DDR5',     '2TB',   'NVIDIA RTX 4090 16GB',    '17 inch QHD 240Hz',          '5 hours',   2.99, 24, 3499.00, NULL,    10, '/images/products/msi-raider-ge78-hx.png',      7, 1,  'ACTIVE', TRUE),
(35, 'MSI Stealth 16 Studio',            'msi-stealth-16-studio',    'Sleek and powerful laptop that combines gaming prowess with professional aesthetics in a remarkably thin chassis.',                                    'Intel Core i7-13700H',     '16GB DDR5',     '1TB',   'NVIDIA RTX 4060 8GB',     '16 inch QHD+ OLED',          '10 hours',  1.99, 24, 1899.00, 1799.00, 20, '/images/products/msi-stealth-16-studio.png',   7, 10, 'ACTIVE', FALSE),
(36, 'MSI Prestige 14',                  'msi-prestige-14',          'Elegant business laptop with premium build quality, vibrant display, and all-day battery for professionals.',                                          'Intel Core Ultra 7 155H',  '16GB LPDDR5',   '512GB', 'Intel Arc Graphics',      '14 inch FHD+',               '14 hours',  1.29, 24, 1199.00, NULL,    30, '/images/products/msi-prestige-14.png',         7, 3,  'ACTIVE', FALSE),
(37, 'MSI Creator Z17 HX Studio',        'msi-creator-z17-hx',       'Desktop-caliber creative workstation with true-to-life display accuracy and exceptional rendering capabilities.',                                     'Intel Core i9-13950HX',    '32GB DDR5',     '2TB',   'NVIDIA RTX 4070 8GB',     '17 inch QHD 165Hz',          '8 hours',   2.49, 24, 2799.00, NULL,    12, '/images/products/msi-creator-z17-hx.png',      7, 7,  'ACTIVE', FALSE),
(38, 'MSI Thin GF63',                    'msi-thin-gf63',            'Budget-friendly gaming laptop with dedicated GPU, high-refresh display, and lightweight design for casual gamers.',                                    'Intel Core i5-12450H',     '8GB DDR4',      '512GB', 'NVIDIA RTX 4050 6GB',     '15.6 inch FHD 144Hz',        '7 hours',   1.86, 24, 699.00,  NULL,    50, '/images/products/msi-thin-gf63.png',           7, 8,  'ACTIVE', FALSE),

-- Gigabyte (brand_id=8)
(39, 'GIGABYTE AERO 16 OLED',            'gigabyte-aero-16-oled',    'Creator-focused laptop with Samsung AMOLED display, AI-powered performance optimization, and studio-grade color accuracy.',                            'Intel Core i9-13900H',     '16GB DDR5',     '1TB',   'NVIDIA RTX 4070 8GB',     '16 inch 4K OLED',            '9 hours',   2.00, 24, 2199.00, NULL,    15, '/images/products/gigabyte-aero-16-oled.png',   8, 7,  'ACTIVE', FALSE),
(40, 'GIGABYTE AORUS 17X',               'gigabyte-aorus-17x',       'Extreme gaming laptop with mechanical keyboard, top-tier GPU, and advanced WINDFORCE cooling system.',                                                'Intel Core i9-13900HX',    '32GB DDR5',     '1TB',   'NVIDIA RTX 4080 12GB',    '17.3 inch QHD 240Hz',        '6 hours',   2.80, 24, 2799.00, NULL,    12, '/images/products/gigabyte-aorus-17x.png',      8, 1,  'ACTIVE', FALSE),
(41, 'GIGABYTE G5',                       'gigabyte-g5',              'Affordable gaming laptop with solid performance, high-refresh display, and WINDFORCE cooling at an attractive price.',                                'Intel Core i5-12500H',     '8GB DDR4',      '512GB', 'NVIDIA RTX 4060 8GB',     '15.6 inch FHD 144Hz',        '6 hours',   2.08, 24, 899.00,  NULL,    40, '/images/products/gigabyte-g5.png',             8, 1,  'ACTIVE', FALSE),
(42, 'GIGABYTE U4',                       'gigabyte-u4',              'Incredibly lightweight ultrabook at under 1kg with long battery life, perfect for maximum portability.',                                               'Intel Core i5-1155G7',     '16GB LPDDR4x',  '512GB', 'Intel Iris Xe',           '14 inch FHD',                '12 hours',  0.99, 24, 899.00,  799.00,  30, '/images/products/gigabyte-u4.png',             8, 10, 'ACTIVE', FALSE),

-- Huawei (brand_id=9)
(43, 'HUAWEI MateBook X Pro 2024',       'matebook-x-pro-2024',      'Flagship ultrabook with stunning 3.1K OLED touchscreen, premium metallic design, and cutting-edge Intel performance.',                                'Intel Core Ultra 9 185H',  '32GB LPDDR5x',  '2TB',   'Intel Arc Graphics',      '14.2 inch 3.1K OLED',        '13 hours',  1.26, 24, 1999.00, NULL,    20, '/images/products/matebook-x-pro-2024.png',     9, 9,  'ACTIVE', TRUE),
(44, 'HUAWEI MateBook D 16 2024',        'matebook-d-16-2024',       'Versatile and affordable 16-inch laptop with fast charging, eye comfort display, and reliable everyday performance.',                                  'Intel Core i5-13500H',     '16GB DDR4',     '512GB', 'Intel Iris Xe',           '16 inch FHD+',               '11 hours',  1.68, 24, 749.00,  NULL,    40, '/images/products/matebook-d-16-2024.png',      9, 6,  'ACTIVE', FALSE),
(45, 'HUAWEI MateBook 14s',              'matebook-14s',             'Well-rounded premium laptop with 2.5K touchscreen, fast performance, and elegant metallic design.',                                                    'Intel Core i7-13700H',     '16GB LPDDR5',   '512GB', 'Intel Iris Xe',           '14.2 inch 2.5K IPS',         '13 hours',  1.43, 24, 1199.00, 1099.00, 25, '/images/products/matebook-14s.png',            9, 2,  'ACTIVE', FALSE),
(46, 'HUAWEI MateBook E Go',             'matebook-e-go',            'Ultra-lightweight detachable 2-in-1 with OLED display, Qualcomm chipset, and exceptional battery life.',                                               'Qualcomm Snapdragon 8cx Gen 3','16GB LPDDR4x','512GB','Qualcomm Adreno',        '12.35 inch 2.5K OLED',       '14 hours',  0.71, 24, 999.00,  NULL,    20, '/images/products/matebook-e-go.png',           9, 5,  'ACTIVE', FALSE),

-- Razer (brand_id=10)
(47, 'Razer Blade 16',                   'razer-blade-16',           'The pinnacle of gaming laptops with a dual-mode 240Hz Mini LED display, top-tier GPU, and CNC aluminum unibody.',                                     'Intel Core i9-13950HX',    '32GB DDR5',     '1TB',   'NVIDIA RTX 4090 16GB',    '16 inch QHD 240Hz',          '5 hours',   2.45, 12, 3999.00, NULL,    8,  '/images/products/razer-blade-16.png',          10, 1,  'ACTIVE', TRUE),
(48, 'Razer Blade 14',                   'razer-blade-14',           'The most powerful 14-inch gaming laptop with AMD Ryzen, QHD display, and premium build in a compact package.',                                         'AMD Ryzen 9 7940HS',       '16GB DDR5',     '1TB',   'NVIDIA RTX 4070 8GB',     '14 inch QHD 240Hz',          '8 hours',   1.84, 12, 2799.00, NULL,    12, '/images/products/razer-blade-14.png',          10, 1,  'ACTIVE', FALSE),
(49, 'Razer Blade 15',                   'razer-blade-15',           'The iconic 15-inch gaming laptop with sleek aluminum design, fast display, and powerful discrete graphics.',                                             'Intel Core i7-13800H',     '16GB DDR5',     '1TB',   'NVIDIA RTX 4060 8GB',     '15.6 inch QHD 240Hz',        '7 hours',   2.04, 12, 2199.00, NULL,    15, '/images/products/razer-blade-15.png',          10, 9,  'ACTIVE', FALSE),
(50, 'Razer Book 13',                    'razer-book-13',            'Productivity-focused ultrabook with 4K touch display, Thunderbolt 4, and the signature Razer premium build quality.',                                   'Intel Core i7-1355U',      '16GB LPDDR5',   '512GB', 'Intel Iris Xe',           '13.4 inch 4K Touch',         '12 hours',  1.36, 12, 1799.00, NULL,    18, '/images/products/razer-book-13.png',           10, 2,  'ACTIVE', FALSE);

-- ----------------------------------------------------------------
-- Product Images (100 images, 2 per product)
-- ----------------------------------------------------------------
INSERT INTO product_images (id, product_id, image_url, display_order) VALUES
(1,  1,  '/images/products/macbook-air-m3-13-1.png',     1),
(2,  1,  '/images/products/macbook-air-m3-13-2.png',     2),
(3,  2,  '/images/products/macbook-air-m3-15-1.png',     1),
(4,  2,  '/images/products/macbook-air-m3-15-2.png',     2),
(5,  3,  '/images/products/macbook-pro-14-m3-1.png',     1),
(6,  3,  '/images/products/macbook-pro-14-m3-2.png',     2),
(7,  4,  '/images/products/macbook-pro-16-m3-pro-1.png', 1),
(8,  4,  '/images/products/macbook-pro-16-m3-pro-2.png', 2),
(9,  5,  '/images/products/macbook-pro-14-m3-max-1.png', 1),
(10, 5,  '/images/products/macbook-pro-14-m3-max-2.png', 2),
(11, 6,  '/images/products/dell-xps-13-plus-1.png',      1),
(12, 6,  '/images/products/dell-xps-13-plus-2.png',      2),
(13, 7,  '/images/products/dell-xps-15-1.png',           1),
(14, 7,  '/images/products/dell-xps-15-2.png',           2),
(15, 8,  '/images/products/dell-xps-17-1.png',           1),
(16, 8,  '/images/products/dell-xps-17-2.png',           2),
(17, 9,  '/images/products/dell-inspiron-16-1.png',      1),
(18, 9,  '/images/products/dell-inspiron-16-2.png',      2),
(19, 10, '/images/products/dell-latitude-14-1.png',      1),
(20, 10, '/images/products/dell-latitude-14-2.png',      2),
(21, 11, '/images/products/dell-precision-5680-1.png',   1),
(22, 11, '/images/products/dell-precision-5680-2.png',   2),
(23, 12, '/images/products/hp-spectre-x360-14-1.png',    1),
(24, 12, '/images/products/hp-spectre-x360-14-2.png',    2),
(25, 13, '/images/products/hp-envy-16-1.png',            1),
(26, 13, '/images/products/hp-envy-16-2.png',            2),
(27, 14, '/images/products/hp-pavilion-15-1.png',        1),
(28, 14, '/images/products/hp-pavilion-15-2.png',        2),
(29, 15, '/images/products/hp-elitebook-840-g10-1.png',  1),
(30, 15, '/images/products/hp-elitebook-840-g10-2.png',  2),
(31, 16, '/images/products/hp-zbook-studio-g10-1.png',   1),
(32, 16, '/images/products/hp-zbook-studio-g10-2.png',   2),
(33, 17, '/images/products/thinkpad-x1-carbon-1.png',    1),
(34, 17, '/images/products/thinkpad-x1-carbon-2.png',    2),
(35, 18, '/images/products/thinkpad-t14s-1.png',         1),
(36, 18, '/images/products/thinkpad-t14s-2.png',         2),
(37, 19, '/images/products/ideapad-5-pro-1.png',         1),
(38, 19, '/images/products/ideapad-5-pro-2.png',         2),
(39, 20, '/images/products/legion-pro-5-16-1.png',       1),
(40, 20, '/images/products/legion-pro-5-16-2.png',       2),
(41, 21, '/images/products/yoga-9i-14-1.png',            1),
(42, 21, '/images/products/yoga-9i-14-2.png',            2),
(43, 22, '/images/products/thinkpad-p16s-1.png',         1),
(44, 22, '/images/products/thinkpad-p16s-2.png',         2),
(45, 23, '/images/products/rog-strix-g16-1.png',         1),
(46, 23, '/images/products/rog-strix-g16-2.png',         2),
(47, 24, '/images/products/zenbook-14-oled-1.png',       1),
(48, 24, '/images/products/zenbook-14-oled-2.png',       2),
(49, 25, '/images/products/vivobook-15-1.png',           1),
(50, 25, '/images/products/vivobook-15-2.png',           2),
(51, 26, '/images/products/tuf-gaming-a15-1.png',        1),
(52, 26, '/images/products/tuf-gaming-a15-2.png',        2),
(53, 27, '/images/products/proart-studiobook-16-1.png',  1),
(54, 27, '/images/products/proart-studiobook-16-2.png',  2),
(55, 28, '/images/products/rog-zephyrus-g14-1.png',      1),
(56, 28, '/images/products/rog-zephyrus-g14-2.png',      2),
(57, 29, '/images/products/predator-helios-16-1.png',    1),
(58, 29, '/images/products/predator-helios-16-2.png',    2),
(59, 30, '/images/products/swift-go-14-1.png',           1),
(60, 30, '/images/products/swift-go-14-2.png',           2),
(61, 31, '/images/products/acer-aspire-5-1.png',         1),
(62, 31, '/images/products/acer-aspire-5-2.png',         2),
(63, 32, '/images/products/acer-nitro-v-15-1.png',       1),
(64, 32, '/images/products/acer-nitro-v-15-2.png',       2),
(65, 33, '/images/products/acer-conceptd-7-1.png',       1),
(66, 33, '/images/products/acer-conceptd-7-2.png',       2),
(67, 34, '/images/products/msi-raider-ge78-hx-1.png',    1),
(68, 34, '/images/products/msi-raider-ge78-hx-2.png',    2),
(69, 35, '/images/products/msi-stealth-16-1.png',        1),
(70, 35, '/images/products/msi-stealth-16-2.png',        2),
(71, 36, '/images/products/msi-prestige-14-1.png',       1),
(72, 36, '/images/products/msi-prestige-14-2.png',       2),
(73, 37, '/images/products/msi-creator-z17-hx-1.png',    1),
(74, 37, '/images/products/msi-creator-z17-hx-2.png',    2),
(75, 38, '/images/products/msi-thin-gf63-1.png',         1),
(76, 38, '/images/products/msi-thin-gf63-2.png',         2),
(77, 39, '/images/products/gigabyte-aero-16-1.png',      1),
(78, 39, '/images/products/gigabyte-aero-16-2.png',      2),
(79, 40, '/images/products/gigabyte-aorus-17x-1.png',    1),
(80, 40, '/images/products/gigabyte-aorus-17x-2.png',    2),
(81, 41, '/images/products/gigabyte-g5-1.png',           1),
(82, 41, '/images/products/gigabyte-g5-2.png',           2),
(83, 42, '/images/products/gigabyte-u4-1.png',           1),
(84, 42, '/images/products/gigabyte-u4-2.png',           2),
(85, 43, '/images/products/matebook-x-pro-2024-1.png',   1),
(86, 43, '/images/products/matebook-x-pro-2024-2.png',   2),
(87, 44, '/images/products/matebook-d-16-1.png',         1),
(88, 44, '/images/products/matebook-d-16-2.png',         2),
(89, 45, '/images/products/matebook-14s-1.png',          1),
(90, 45, '/images/products/matebook-14s-2.png',          2),
(91, 46, '/images/products/matebook-e-go-1.png',         1),
(92, 46, '/images/products/matebook-e-go-2.png',         2),
(93, 47, '/images/products/razer-blade-16-1.png',        1),
(94, 47, '/images/products/razer-blade-16-2.png',        2),
(95, 48, '/images/products/razer-blade-14-1.png',        1),
(96, 48, '/images/products/razer-blade-14-2.png',        2),
(97, 49, '/images/products/razer-blade-15-1.png',        1),
(98, 49, '/images/products/razer-blade-15-2.png',        2),
(99, 50, '/images/products/razer-book-13-1.png',         1),
(100,50, '/images/products/razer-book-13-2.png',         2);

-- ----------------------------------------------------------------
-- Wishlists (10 wishlists, one per user)
-- ----------------------------------------------------------------
INSERT INTO wishlists (id, user_id, name) VALUES
(1,  2,  'My Wishlist'),
(2,  3,  'My Wishlist'),
(3,  4,  'My Wishlist'),
(4,  5,  'My Wishlist'),
(5,  6,  'My Wishlist'),
(6,  7,  'My Wishlist'),
(7,  8,  'My Wishlist'),
(8,  9,  'My Wishlist'),
(9,  10, 'My Wishlist'),
(10, 11, 'My Wishlist');

-- ----------------------------------------------------------------
-- Wishlist Items (~25 items)
-- ----------------------------------------------------------------
INSERT INTO wishlist_items (id, wishlist_id, product_id) VALUES
(1,  1, 3),  (2,  1, 20), (3,  1, 47),
(4,  2, 1),  (5,  2, 24), (6,  2, 43),
(7,  3, 5),  (8,  3, 17), (9,  3, 34),
(10, 4, 12), (11, 4, 28),
(12, 5, 7),  (13, 5, 23), (14, 5, 35),
(15, 6, 6),  (16, 6, 29),
(17, 7, 4),  (18, 7, 40),
(19, 8, 15), (20, 8, 27), (21, 8, 48),
(22, 9, 11), (23, 9, 37),
(24, 10, 2), (25, 10, 50);

-- ----------------------------------------------------------------
-- Carts (10 carts, one per user)
-- ----------------------------------------------------------------
INSERT INTO carts (id, user_id) VALUES
(1, 2), (2, 3), (3, 4), (4, 5), (5, 6),
(6, 7), (7, 8), (8, 9), (9, 10), (10, 11);

-- ----------------------------------------------------------------
-- Cart Items (~20 items)
-- ----------------------------------------------------------------
INSERT INTO cart_items (id, cart_id, product_id, quantity) VALUES
(1,  1, 4,  1), (2,  1, 24, 1),
(3,  2, 20, 1), (4,  2, 28, 1),
(5,  3, 1,  1), (6,  3, 47, 1),
(7,  4, 12, 1),
(8,  5, 34, 1), (9,  5, 43, 1),
(10, 6, 6,  1), (11, 6, 17, 1),
(12, 7, 29, 1),
(13, 8, 3,  1), (14, 8, 35, 1),
(15, 9, 23, 1), (16, 9, 50, 1),
(17, 10, 7, 1), (18, 10, 45, 1);

-- ----------------------------------------------------------------
-- Coupons (10 coupons)
-- ----------------------------------------------------------------
INSERT INTO coupons (id, code, description, discount_type, discount_value, min_order_amount, max_discount_amount, usage_limit, used_count, start_date, end_date, status) VALUES
(1,  'WELCOME10',    '10% off for new customers',           'PERCENTAGE', 10.00,  500.00,  500.00,  100, 15, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 'ACTIVE'),
(2,  'SAVE50',       '$50 off on orders over $1000',        'FIXED',      50.00,  1000.00, NULL,    200, 30, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 'ACTIVE'),
(3,  'MEGA15',       '15% off mega sale',                   'PERCENTAGE', 15.00,  2000.00, 1000.00, 50,  8,  '2024-06-01 00:00:00', '2025-06-30 23:59:59', 'ACTIVE'),
(4,  'FLASH100',     '$100 off flash deal',                 'FIXED',      100.00, 2000.00, NULL,    30,  12, '2024-03-01 00:00:00', '2025-03-31 23:59:59', 'ACTIVE'),
(5,  'LOYAL5',       '5% off for loyal customers',          'PERCENTAGE', 5.00,   1000.00, 300.00,  0,   5,  '2024-01-01 00:00:00', '2025-12-31 23:59:59', 'ACTIVE'),
(6,  'SUMMER20',     '20% off summer sale',                 'PERCENTAGE', 20.00,  1500.00, 600.00,  80,  20, '2024-06-01 00:00:00', '2024-08-31 23:59:59', 'EXPIRED'),
(7,  'NEWUSER',      '10% off first purchase',              'PERCENTAGE', 10.00,  0.00,    200.00,  500, 45, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 'ACTIVE'),
(8,  'HOLIDAY25',    '25% off holiday special',             'PERCENTAGE', 25.00,  3000.00, 800.00,  40,  10, '2024-12-01 00:00:00', '2025-01-31 23:59:59', 'ACTIVE'),
(9,  'TECHSALE',     '$200 off on premium laptops',         'FIXED',      200.00, 2000.00, NULL,    60,  18, '2024-01-01 00:00:00', '2025-12-31 23:59:59', 'ACTIVE'),
(10, 'BACKTOSCHOOL', '15% off for students',                'PERCENTAGE', 15.00,  500.00,  400.00,  100, 25, '2024-08-01 00:00:00', '2024-09-30 23:59:59', 'EXPIRED');

-- ----------------------------------------------------------------
-- Orders (50 orders)
-- Status: 1-15 DELIVERED, 16-25 SHIPPING, 26-33 PROCESSING,
--         34-38 CONFIRMED, 39-43 PENDING, 44-48 CANCELLED, 49-50 RETURNED
-- ----------------------------------------------------------------
INSERT INTO orders (id, user_id, order_code, total_amount, discount_amount, final_amount, note, coupon_id, created_at) VALUES
-- User 2 (orders 1-5, DELIVERED)
(1, 2, 'ORD-2024-00001', 2398.00, 0.00, 2398.00, NULL, NULL, '2024-01-15 10:30:00'),
(2, 2, 'ORD-2024-00002', 4098.00, 0.00, 4098.00, 'Please deliver before 5pm', NULL, '2024-02-10 14:20:00'),
(3, 2, 'ORD-2024-00003', 4798.00, 479.80, 4318.20, NULL, 1, '2024-03-05 09:15:00'),
(4, 2, 'ORD-2024-00004', 4198.00, 0.00, 4198.00, NULL, NULL, '2024-04-12 16:45:00'),
(5, 2, 'ORD-2024-00005', 2048.00, 0.00, 2048.00, 'Leave at front door', NULL, '2024-05-20 11:00:00'),

-- User 3 (orders 6-10, DELIVERED)
(6, 3, 'ORD-2024-00006', 4898.00, 0.00, 4898.00, NULL, NULL, '2024-01-22 08:30:00'),
(7, 3, 'ORD-2024-00007', 2048.00, 0.00, 2048.00, NULL, NULL, '2024-02-28 13:10:00'),
(8, 3, 'ORD-2024-00008', 5498.00, 0.00, 5498.00, 'Gift wrapping please', NULL, '2024-03-15 10:00:00'),
(9, 3, 'ORD-2024-00009', 3248.00, 0.00, 3248.00, NULL, NULL, '2024-04-20 15:30:00'),
(10, 3, 'ORD-2024-00010', 2698.00, 50.00, 2648.00, NULL, 2, '2024-05-10 09:45:00'),

-- User 4 (orders 11-15, DELIVERED)
(11, 4, 'ORD-2024-00011', 3498.00, 0.00, 3498.00, NULL, NULL, '2024-02-05 11:20:00'),
(12, 4, 'ORD-2024-00012', 2998.00, 0.00, 2998.00, NULL, NULL, '2024-03-10 14:00:00'),
(13, 4, 'ORD-2024-00013', 1548.00, 0.00, 1548.00, 'Company order', NULL, '2024-04-18 10:30:00'),
(14, 4, 'ORD-2024-00014', 4098.00, 0.00, 4098.00, NULL, NULL, '2024-05-25 16:00:00'),
(15, 4, 'ORD-2024-00015', 3398.00, 0.00, 3398.00, NULL, NULL, '2024-06-12 08:45:00'),

-- User 5 (orders 16-20, SHIPPING)
(16, 5, 'ORD-2024-00016', 1298.00, 0.00, 1298.00, NULL, NULL, '2024-07-01 10:00:00'),
(17, 5, 'ORD-2024-00017', 6498.00, 974.70, 5523.30, 'Handle with care', 3, '2024-07-05 14:30:00'),
(18, 5, 'ORD-2024-00018', 3098.00, 0.00, 3098.00, NULL, NULL, '2024-07-10 09:00:00'),
(19, 5, 'ORD-2024-00019', 3498.00, 0.00, 3498.00, NULL, NULL, '2024-07-15 11:30:00'),
(20, 5, 'ORD-2024-00020', 4998.00, 0.00, 4998.00, NULL, NULL, '2024-07-20 15:00:00'),

-- User 6 (orders 21-25, SHIPPING)
(21, 6, 'ORD-2024-00021', 1798.00, 0.00, 1798.00, NULL, NULL, '2024-07-02 10:30:00'),
(22, 6, 'ORD-2024-00022', 2748.00, 0.00, 2748.00, NULL, NULL, '2024-07-08 13:00:00'),
(23, 6, 'ORD-2024-00023', 2198.00, 0.00, 2198.00, NULL, NULL, '2024-07-12 09:45:00'),
(24, 6, 'ORD-2024-00024', 6798.00, 0.00, 6798.00, 'Urgent delivery', NULL, '2024-07-18 16:30:00'),
(25, 6, 'ORD-2024-00025', 3998.00, 100.00, 3898.00, NULL, 4, '2024-07-22 11:00:00'),

-- User 7 (orders 26-30, PROCESSING)
(26, 7, 'ORD-2024-00026', 2498.00, 0.00, 2498.00, NULL, NULL, '2024-08-01 10:00:00'),
(27, 7, 'ORD-2024-00027', 3198.00, 0.00, 3198.00, NULL, NULL, '2024-08-05 14:30:00'),
(28, 7, 'ORD-2024-00028', 2598.00, 0.00, 2598.00, NULL, NULL, '2024-08-10 09:15:00'),
(29, 7, 'ORD-2024-00029', 5298.00, 0.00, 5298.00, 'Call before delivery', NULL, '2024-08-15 11:00:00'),
(30, 7, 'ORD-2024-00030', 5298.00, 0.00, 5298.00, NULL, NULL, '2024-08-20 15:30:00'),

-- User 8 (orders 31-35, 31-33 PROCESSING, 34-35 CONFIRMED)
(31, 8, 'ORD-2024-00031', 2898.00, 0.00, 2898.00, NULL, NULL, '2024-08-02 10:30:00'),
(32, 8, 'ORD-2024-00032', 2148.00, 0.00, 2148.00, NULL, NULL, '2024-08-08 13:00:00'),
(33, 8, 'ORD-2024-00033', 4398.00, 0.00, 4398.00, NULL, NULL, '2024-08-12 09:45:00'),
(34, 8, 'ORD-2024-00034', 1848.00, 0.00, 1848.00, NULL, NULL, '2024-08-18 16:30:00'),
(35, 8, 'ORD-2024-00035', 5098.00, 0.00, 5098.00, NULL, NULL, '2024-08-22 11:00:00'),

-- User 9 (orders 36-40, 36-38 CONFIRMED, 39-40 PENDING)
(36, 9, 'ORD-2024-00036', 5098.00, 254.90, 4843.10, NULL, 5, '2024-09-01 10:00:00'),
(37, 9, 'ORD-2024-00037', 3498.00, 0.00, 3498.00, NULL, NULL, '2024-09-05 14:30:00'),
(38, 9, 'ORD-2024-00038', 1298.00, 0.00, 1298.00, NULL, NULL, '2024-09-10 09:15:00'),
(39, 9, 'ORD-2024-00039', 4898.00, 0.00, 4898.00, NULL, NULL, '2024-09-15 11:00:00'),
(40, 9, 'ORD-2024-00040', 5748.00, 0.00, 5748.00, 'Weekend delivery preferred', NULL, '2024-09-20 15:30:00'),

-- User 10 (orders 41-45, 41-43 PENDING, 44-45 CANCELLED)
(41, 10, 'ORD-2024-00041', 4298.00, 0.00, 4298.00, NULL, NULL, '2024-09-25 10:30:00'),
(42, 10, 'ORD-2024-00042', 2998.00, 0.00, 2998.00, NULL, NULL, '2024-10-01 13:00:00'),
(43, 10, 'ORD-2024-00043', 2098.00, 0.00, 2098.00, NULL, NULL, '2024-10-05 09:45:00'),
(44, 10, 'ORD-2024-00044', 2698.00, 0.00, 2698.00, 'Cancelled - wrong address', NULL, '2024-10-10 16:30:00'),
(45, 10, 'ORD-2024-00045', 2398.00, 0.00, 2398.00, 'Cancelled - found better deal', NULL, '2024-10-15 11:00:00'),

-- User 11 (orders 46-50, 46-48 CANCELLED, 49-50 RETURNED)
(46, 11, 'ORD-2024-00046', 3898.00, 0.00, 3898.00, 'Cancelled by customer', NULL, '2024-10-20 10:00:00'),
(47, 11, 'ORD-2024-00047', 3198.00, 0.00, 3198.00, 'Cancelled - duplicate', NULL, '2024-10-25 14:30:00'),
(48, 11, 'ORD-2024-00048', 3798.00, 0.00, 3798.00, 'Cancelled', NULL, '2024-11-01 09:15:00'),
(49, 11, 'ORD-2024-00049', 3298.00, 0.00, 3298.00, 'Returned - defective item', NULL, '2024-11-05 11:00:00'),
(50, 11, 'ORD-2024-00050', 3898.00, 0.00, 3898.00, 'Returned - not as described', NULL, '2024-11-10 15:30:00');

-- ----------------------------------------------------------------
-- Order Items (100 items, 2 per order)
-- ----------------------------------------------------------------
INSERT INTO order_items (id, order_id, product_id, product_name, product_price, quantity, subtotal) VALUES
-- Orders 1-5 (User 2)
(1,  1,  1,  'MacBook Air M3 13 inch',               1099.00, 1, 1099.00),
(2,  1,  2,  'MacBook Air M3 15 inch',               1299.00, 1, 1299.00),
(3,  2,  3,  'MacBook Pro 14 inch M3',               1599.00, 1, 1599.00),
(4,  2,  4,  'MacBook Pro 16 inch M3 Pro',           2499.00, 1, 2499.00),
(5,  3,  5,  'MacBook Pro 14 inch M3 Max',           3499.00, 1, 3499.00),
(6,  3,  6,  'Dell XPS 13 Plus',                     1299.00, 1, 1299.00),
(7,  4,  7,  'Dell XPS 15',                          1699.00, 1, 1699.00),
(8,  4,  8,  'Dell XPS 17',                          2499.00, 1, 2499.00),
(9,  5,  9,  'Dell Inspiron 16',                     649.00,  1, 649.00),
(10, 5,  10, 'Dell Latitude 14',                     1399.00, 1, 1399.00),

-- Orders 6-10 (User 3)
(11, 6,  11, 'Dell Precision 5680',                  3299.00, 1, 3299.00),
(12, 6,  12, 'HP Spectre x360 14',                   1599.00, 1, 1599.00),
(13, 7,  13, 'HP Envy 16',                           1499.00, 1, 1499.00),
(14, 7,  14, 'HP Pavilion 15',                       549.00,  1, 549.00),
(15, 8,  15, 'HP EliteBook 840 G10',                 1599.00, 1, 1599.00),
(16, 8,  16, 'HP ZBook Studio G10',                  3899.00, 1, 3899.00),
(17, 9,  17, 'Lenovo ThinkPad X1 Carbon Gen 11',     1749.00, 1, 1749.00),
(18, 9,  18, 'Lenovo ThinkPad T14s Gen 4',           1499.00, 1, 1499.00),
(19, 10, 19, 'Lenovo IdeaPad 5 Pro',                 799.00,  1, 799.00),
(20, 10, 20, 'Lenovo Legion Pro 5 16',               1899.00, 1, 1899.00),

-- Orders 11-15 (User 4)
(21, 11, 21, 'Lenovo Yoga 9i 14',                    1599.00, 1, 1599.00),
(22, 11, 22, 'Lenovo ThinkPad P16s Gen 2',           1899.00, 1, 1899.00),
(23, 12, 23, 'ASUS ROG Strix G16',                   1799.00, 1, 1799.00),
(24, 12, 24, 'ASUS ZenBook 14 OLED',                 1199.00, 1, 1199.00),
(25, 13, 25, 'ASUS VivoBook 15',                     449.00,  1, 449.00),
(26, 13, 26, 'ASUS TUF Gaming A15',                  1099.00, 1, 1099.00),
(27, 14, 27, 'ASUS ProArt Studiobook 16 OLED',       2499.00, 1, 2499.00),
(28, 14, 28, 'ASUS ROG Zephyrus G14',                1599.00, 1, 1599.00),
(29, 15, 29, 'Acer Predator Helios 16',              2399.00, 1, 2399.00),
(30, 15, 30, 'Acer Swift Go 14',                     999.00,  1, 999.00),

-- Orders 16-20 (User 5)
(31, 16, 31, 'Acer Aspire 5',                        499.00,  1, 499.00),
(32, 16, 32, 'Acer Nitro V 15',                      799.00,  1, 799.00),
(33, 17, 33, 'Acer ConceptD 7',                      2999.00, 1, 2999.00),
(34, 17, 34, 'MSI Raider GE78 HX',                   3499.00, 1, 3499.00),
(35, 18, 35, 'MSI Stealth 16 Studio',                1899.00, 1, 1899.00),
(36, 18, 36, 'MSI Prestige 14',                      1199.00, 1, 1199.00),
(37, 19, 37, 'MSI Creator Z17 HX Studio',            2799.00, 1, 2799.00),
(38, 19, 38, 'MSI Thin GF63',                        699.00,  1, 699.00),
(39, 20, 39, 'GIGABYTE AERO 16 OLED',                2199.00, 1, 2199.00),
(40, 20, 40, 'GIGABYTE AORUS 17X',                   2799.00, 1, 2799.00),

-- Orders 21-25 (User 6)
(41, 21, 41, 'GIGABYTE G5',                           899.00, 1, 899.00),
(42, 21, 42, 'GIGABYTE U4',                           899.00, 1, 899.00),
(43, 22, 43, 'HUAWEI MateBook X Pro 2024',           1999.00, 1, 1999.00),
(44, 22, 44, 'HUAWEI MateBook D 16 2024',            749.00,  1, 749.00),
(45, 23, 45, 'HUAWEI MateBook 14s',                  1199.00, 1, 1199.00),
(46, 23, 46, 'HUAWEI MateBook E Go',                 999.00,  1, 999.00),
(47, 24, 47, 'Razer Blade 16',                       3999.00, 1, 3999.00),
(48, 24, 48, 'Razer Blade 14',                       2799.00, 1, 2799.00),
(49, 25, 49, 'Razer Blade 15',                       2199.00, 1, 2199.00),
(50, 25, 50, 'Razer Book 13',                        1799.00, 1, 1799.00),

-- Orders 26-30 (User 7)
(51, 26, 1,  'MacBook Air M3 13 inch',               1099.00, 1, 1099.00),
(52, 26, 10, 'Dell Latitude 14',                     1399.00, 1, 1399.00),
(53, 27, 2,  'MacBook Air M3 15 inch',               1299.00, 1, 1299.00),
(54, 27, 20, 'Lenovo Legion Pro 5 16',               1899.00, 1, 1899.00),
(55, 28, 3,  'MacBook Pro 14 inch M3',               1599.00, 1, 1599.00),
(56, 28, 30, 'Acer Swift Go 14',                     999.00,  1, 999.00),
(57, 29, 4,  'MacBook Pro 16 inch M3 Pro',           2499.00, 1, 2499.00),
(58, 29, 40, 'GIGABYTE AORUS 17X',                   2799.00, 1, 2799.00),
(59, 30, 5,  'MacBook Pro 14 inch M3 Max',           3499.00, 1, 3499.00),
(60, 30, 50, 'Razer Book 13',                        1799.00, 1, 1799.00),

-- Orders 31-35 (User 8)
(61, 31, 6,  'Dell XPS 13 Plus',                     1299.00, 1, 1299.00),
(62, 31, 15, 'HP EliteBook 840 G10',                 1599.00, 1, 1599.00),
(63, 32, 7,  'Dell XPS 15',                          1699.00, 1, 1699.00),
(64, 32, 25, 'ASUS VivoBook 15',                     449.00,  1, 449.00),
(65, 33, 8,  'Dell XPS 17',                          2499.00, 1, 2499.00),
(66, 33, 35, 'MSI Stealth 16 Studio',                1899.00, 1, 1899.00),
(67, 34, 9,  'Dell Inspiron 16',                     649.00,  1, 649.00),
(68, 34, 45, 'HUAWEI MateBook 14s',                  1199.00, 1, 1199.00),
(69, 35, 11, 'Dell Precision 5680',                  3299.00, 1, 3299.00),
(70, 35, 23, 'ASUS ROG Strix G16',                   1799.00, 1, 1799.00),

-- Orders 36-40 (User 9)
(71, 36, 12, 'HP Spectre x360 14',                   1599.00, 1, 1599.00),
(72, 36, 34, 'MSI Raider GE78 HX',                   3499.00, 1, 3499.00),
(73, 37, 13, 'HP Envy 16',                           1499.00, 1, 1499.00),
(74, 37, 43, 'HUAWEI MateBook X Pro 2024',           1999.00, 1, 1999.00),
(75, 38, 14, 'HP Pavilion 15',                       549.00,  1, 549.00),
(76, 38, 44, 'HUAWEI MateBook D 16 2024',            749.00,  1, 749.00),
(77, 39, 16, 'HP ZBook Studio G10',                  3899.00, 1, 3899.00),
(78, 39, 46, 'HUAWEI MateBook E Go',                 999.00,  1, 999.00),
(79, 40, 17, 'Lenovo ThinkPad X1 Carbon Gen 11',     1749.00, 1, 1749.00),
(80, 40, 47, 'Razer Blade 16',                       3999.00, 1, 3999.00),

-- Orders 41-45 (User 10)
(81, 41, 18, 'Lenovo ThinkPad T14s Gen 4',           1499.00, 1, 1499.00),
(82, 41, 48, 'Razer Blade 14',                       2799.00, 1, 2799.00),
(83, 42, 19, 'Lenovo IdeaPad 5 Pro',                 799.00,  1, 799.00),
(84, 42, 49, 'Razer Blade 15',                       2199.00, 1, 2199.00),
(85, 43, 21, 'Lenovo Yoga 9i 14',                    1599.00, 1, 1599.00),
(86, 43, 31, 'Acer Aspire 5',                        499.00,  1, 499.00),
(87, 44, 22, 'Lenovo ThinkPad P16s Gen 2',           1899.00, 1, 1899.00),
(88, 44, 32, 'Acer Nitro V 15',                      799.00,  1, 799.00),
(89, 45, 24, 'ASUS ZenBook 14 OLED',                 1199.00, 1, 1199.00),
(90, 45, 36, 'MSI Prestige 14',                      1199.00, 1, 1199.00),

-- Orders 46-50 (User 11)
(91, 46, 26, 'ASUS TUF Gaming A15',                  1099.00, 1, 1099.00),
(92, 46, 37, 'MSI Creator Z17 HX Studio',            2799.00, 1, 2799.00),
(93, 47, 27, 'ASUS ProArt Studiobook 16 OLED',       2499.00, 1, 2499.00),
(94, 47, 38, 'MSI Thin GF63',                        699.00,  1, 699.00),
(95, 48, 28, 'ASUS ROG Zephyrus G14',                1599.00, 1, 1599.00),
(96, 48, 39, 'GIGABYTE AERO 16 OLED',                2199.00, 1, 2199.00),
(97, 49, 29, 'Acer Predator Helios 16',              2399.00, 1, 2399.00),
(98, 49, 41, 'GIGABYTE G5',                           899.00, 1, 899.00),
(99, 50, 33, 'Acer ConceptD 7',                      2999.00, 1, 2999.00),
(100,50, 42, 'GIGABYTE U4',                           899.00, 1, 899.00);

-- ----------------------------------------------------------------
-- Payments (50 payments, one per order)
-- DELIVERED/SHIPPING/PROCESSING/CONFIRMED: PAID
-- PENDING: UNPAID
-- CANCELLED: REFUNDED
-- RETURNED: REFUNDED
-- ----------------------------------------------------------------
INSERT INTO payments (id, order_id, method, amount, status, transaction_id, paid_at) VALUES
(1,  1,  'COD', 2398.00, 'PAID',     'TXN-2024-00001', '2024-01-20 14:00:00'),
(2,  2,  'COD', 4098.00, 'PAID',     'TXN-2024-00002', '2024-02-15 10:30:00'),
(3,  3,  'COD', 4318.20, 'PAID',     'TXN-2024-00003', '2024-03-10 16:00:00'),
(4,  4,  'COD', 4198.00, 'PAID',     'TXN-2024-00004', '2024-04-17 11:00:00'),
(5,  5,  'COD', 2048.00, 'PAID',     'TXN-2024-00005', '2024-05-25 09:30:00'),
(6,  6,  'COD', 4898.00, 'PAID',     'TXN-2024-00006', '2024-01-27 15:00:00'),
(7,  7,  'COD', 2048.00, 'PAID',     'TXN-2024-00007', '2024-03-05 10:00:00'),
(8,  8,  'COD', 5498.00, 'PAID',     'TXN-2024-00008', '2024-03-20 14:30:00'),
(9,  9,  'COD', 3248.00, 'PAID',     'TXN-2024-00009', '2024-04-25 11:00:00'),
(10, 10, 'COD', 2648.00, 'PAID',     'TXN-2024-00010', '2024-05-15 16:00:00'),
(11, 11, 'COD', 3498.00, 'PAID',     'TXN-2024-00011', '2024-02-10 10:30:00'),
(12, 12, 'COD', 2998.00, 'PAID',     'TXN-2024-00012', '2024-03-15 14:00:00'),
(13, 13, 'COD', 1548.00, 'PAID',     'TXN-2024-00013', '2024-04-23 09:45:00'),
(14, 14, 'COD', 4098.00, 'PAID',     'TXN-2024-00014', '2024-05-30 15:30:00'),
(15, 15, 'COD', 3398.00, 'PAID',     'TXN-2024-00015', '2024-06-17 11:00:00'),
(16, 16, 'COD', 1298.00, 'PAID',     'TXN-2024-00016', '2024-07-03 10:00:00'),
(17, 17, 'COD', 5523.30, 'PAID',     'TXN-2024-00017', '2024-07-07 14:30:00'),
(18, 18, 'COD', 3098.00, 'PAID',     'TXN-2024-00018', '2024-07-12 09:00:00'),
(19, 19, 'COD', 3498.00, 'PAID',     'TXN-2024-00019', '2024-07-17 11:30:00'),
(20, 20, 'COD', 4998.00, 'PAID',     'TXN-2024-00020', '2024-07-22 15:00:00'),
(21, 21, 'COD', 1798.00, 'PAID',     'TXN-2024-00021', '2024-07-04 10:30:00'),
(22, 22, 'COD', 2748.00, 'PAID',     'TXN-2024-00022', '2024-07-10 13:00:00'),
(23, 23, 'COD', 2198.00, 'PAID',     'TXN-2024-00023', '2024-07-14 09:45:00'),
(24, 24, 'COD', 6798.00, 'PAID',     'TXN-2024-00024', '2024-07-20 16:30:00'),
(25, 25, 'COD', 3898.00, 'PAID',     'TXN-2024-00025', '2024-07-24 11:00:00'),
(26, 26, 'COD', 2498.00, 'PAID',     'TXN-2024-00026', '2024-08-03 10:00:00'),
(27, 27, 'COD', 3198.00, 'PAID',     'TXN-2024-00027', '2024-08-07 14:30:00'),
(28, 28, 'COD', 2598.00, 'PAID',     'TXN-2024-00028', '2024-08-12 09:15:00'),
(29, 29, 'COD', 5298.00, 'PAID',     'TXN-2024-00029', '2024-08-17 11:00:00'),
(30, 30, 'COD', 5298.00, 'PAID',     'TXN-2024-00030', '2024-08-22 15:30:00'),
(31, 31, 'COD', 2898.00, 'PAID',     'TXN-2024-00031', '2024-08-04 10:30:00'),
(32, 32, 'COD', 2148.00, 'PAID',     'TXN-2024-00032', '2024-08-10 13:00:00'),
(33, 33, 'COD', 4398.00, 'PAID',     'TXN-2024-00033', '2024-08-14 09:45:00'),
(34, 34, 'COD', 1848.00, 'PAID',     'TXN-2024-00034', '2024-08-20 16:30:00'),
(35, 35, 'COD', 5098.00, 'PAID',     'TXN-2024-00035', '2024-08-24 11:00:00'),
(36, 36, 'COD', 4843.10, 'PAID',     'TXN-2024-00036', '2024-09-03 10:00:00'),
(37, 37, 'COD', 3498.00, 'PAID',     'TXN-2024-00037', '2024-09-07 14:30:00'),
(38, 38, 'COD', 1298.00, 'PAID',     'TXN-2024-00038', '2024-09-12 09:15:00'),
(39, 39, 'COD', 4898.00, 'UNPAID',   NULL,              NULL),
(40, 40, 'COD', 5748.00, 'UNPAID',   NULL,              NULL),
(41, 41, 'COD', 4298.00, 'UNPAID',   NULL,              NULL),
(42, 42, 'COD', 2998.00, 'UNPAID',   NULL,              NULL),
(43, 43, 'COD', 2098.00, 'UNPAID',   NULL,              NULL),
(44, 44, 'COD', 2698.00, 'REFUNDED', 'TXN-2024-00044', '2024-10-12 10:00:00'),
(45, 45, 'COD', 2398.00, 'REFUNDED', 'TXN-2024-00045', '2024-10-17 14:00:00'),
(46, 46, 'COD', 3898.00, 'REFUNDED', 'TXN-2024-00046', '2024-10-22 10:00:00'),
(47, 47, 'COD', 3198.00, 'REFUNDED', 'TXN-2024-00047', '2024-10-27 14:30:00'),
(48, 48, 'COD', 3798.00, 'REFUNDED', 'TXN-2024-00048', '2024-11-03 09:15:00'),
(49, 49, 'COD', 3298.00, 'REFUNDED', 'TXN-2024-00049', '2024-11-07 11:00:00'),
(50, 50, 'COD', 3898.00, 'REFUNDED', 'TXN-2024-00050', '2024-11-12 15:30:00');

-- ----------------------------------------------------------------
-- Shipments (for orders that progressed beyond CONFIRMED)
-- DELIVERED orders 1-15: DELIVERED
-- SHIPPING orders 16-25: SHIPPING
-- PROCESSING orders 26-33: READY
-- RETURNED orders 49-50: DELIVERED (before return)
-- ----------------------------------------------------------------




-- ----------------------------------------------------------------
-- Shipments
-- ----------------------------------------------------------------
INSERT INTO shipments (id, order_id, receiver_name, receiver_phone, receiver_address, tracking_number, shipping_provider, shipping_fee, status, shipped_at, delivered_at, created_at, updated_at) VALUES
(1, 1, 'Nguyen Van An', '0901000001', '123 Le Loi, Ben Thanh, Quan 1, Ho Chi Minh', 'TRK-1', 'FastDelivery', 0.00, 'DELIVERED', '2024-01-15 10:30:00', '2024-01-15 10:30:00', '2024-01-15 10:30:00', '2024-01-15 10:30:00'),
(2, 2, 'Nguyen Van An', '0901000001', '123 Le Loi, Ben Thanh, Quan 1, Ho Chi Minh', 'TRK-2', 'FastDelivery', 0.00, 'DELIVERED', '2024-02-10 14:20:00', '2024-02-10 14:20:00', '2024-02-10 14:20:00', '2024-02-10 14:20:00'),
(3, 3, 'Nguyen Van An', '0901000001', '456 Tran Hung Dao, Pham Ngu Lao, Quan 1, Ho Chi Minh', 'TRK-3', 'FastDelivery', 0.00, 'DELIVERED', '2024-03-05 09:15:00', '2024-03-05 09:15:00', '2024-03-05 09:15:00', '2024-03-05 09:15:00'),
(4, 4, 'Nguyen Van An', '0901000001', '123 Le Loi, Ben Thanh, Quan 1, Ho Chi Minh', 'TRK-4', 'FastDelivery', 0.00, 'DELIVERED', '2024-04-12 16:45:00', '2024-04-12 16:45:00', '2024-04-12 16:45:00', '2024-04-12 16:45:00'),
(5, 5, 'Nguyen Van An', '0901000001', '123 Le Loi, Ben Thanh, Quan 1, Ho Chi Minh', 'TRK-5', 'FastDelivery', 0.00, 'DELIVERED', '2024-05-20 11:00:00', '2024-05-20 11:00:00', '2024-05-20 11:00:00', '2024-05-20 11:00:00'),
(6, 6, 'Tran Thi Bich', '0901000002', '789 Hai Ba Trung, Tan Dinh, Quan 1, Ho Chi Minh', 'TRK-6', 'FastDelivery', 0.00, 'DELIVERED', '2024-01-22 08:30:00', '2024-01-22 08:30:00', '2024-01-22 08:30:00', '2024-01-22 08:30:00'),
(7, 7, 'Tran Thi Bich', '0901000002', '789 Hai Ba Trung, Tan Dinh, Quan 1, Ho Chi Minh', 'TRK-7', 'FastDelivery', 0.00, 'DELIVERED', '2024-02-28 13:10:00', '2024-02-28 13:10:00', '2024-02-28 13:10:00', '2024-02-28 13:10:00'),
(8, 8, 'Tran Thi Bich', '0901000002', '789 Hai Ba Trung, Tan Dinh, Quan 1, Ho Chi Minh', 'TRK-8', 'FastDelivery', 0.00, 'DELIVERED', '2024-03-15 10:00:00', '2024-03-15 10:00:00', '2024-03-15 10:00:00', '2024-03-15 10:00:00'),
(9, 9, 'Tran Thi Bich', '0901000002', '789 Hai Ba Trung, Tan Dinh, Quan 1, Ho Chi Minh', 'TRK-9', 'FastDelivery', 0.00, 'DELIVERED', '2024-04-20 15:30:00', '2024-04-20 15:30:00', '2024-04-20 15:30:00', '2024-04-20 15:30:00'),
(10, 10, 'Tran Thi Bich', '0901000002', '789 Hai Ba Trung, Tan Dinh, Quan 1, Ho Chi Minh', 'TRK-10', 'FastDelivery', 0.00, 'DELIVERED', '2024-05-10 09:45:00', '2024-05-10 09:45:00', '2024-05-10 09:45:00', '2024-05-10 09:45:00'),
(11, 11, 'Le Van Cuong', '0901000003', '12 Phan Chu Trinh, Hoan Kiem, Hoan Kiem, Ha Noi', 'TRK-11', 'FastDelivery', 0.00, 'DELIVERED', '2024-02-05 11:20:00', '2024-02-05 11:20:00', '2024-02-05 11:20:00', '2024-02-05 11:20:00'),
(12, 12, 'Le Van Cuong', '0901000003', '12 Phan Chu Trinh, Hoan Kiem, Hoan Kiem, Ha Noi', 'TRK-12', 'FastDelivery', 0.00, 'DELIVERED', '2024-03-10 14:00:00', '2024-03-10 14:00:00', '2024-03-10 14:00:00', '2024-03-10 14:00:00'),
(13, 13, 'Le Van Cuong', '0901000003', '12 Phan Chu Trinh, Hoan Kiem, Hoan Kiem, Ha Noi', 'TRK-13', 'FastDelivery', 0.00, 'DELIVERED', '2024-04-18 10:30:00', '2024-04-18 10:30:00', '2024-04-18 10:30:00', '2024-04-18 10:30:00'),
(14, 14, 'Le Van Cuong', '0901000003', '12 Phan Chu Trinh, Hoan Kiem, Hoan Kiem, Ha Noi', 'TRK-14', 'FastDelivery', 0.00, 'DELIVERED', '2024-05-25 16:00:00', '2024-05-25 16:00:00', '2024-05-25 16:00:00', '2024-05-25 16:00:00'),
(15, 15, 'Le Van Cuong', '0901000003', '12 Phan Chu Trinh, Hoan Kiem, Hoan Kiem, Ha Noi', 'TRK-15', 'FastDelivery', 0.00, 'DELIVERED', '2024-06-12 08:45:00', '2024-06-12 08:45:00', '2024-06-12 08:45:00', '2024-06-12 08:45:00'),
(16, 16, 'Pham Thi Dung', '0901000004', '34 Kim Ma, Giang Vo, Ba Dinh, Ha Noi', 'TRK-16', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-01 10:00:00', NULL, '2024-07-01 10:00:00', '2024-07-01 10:00:00'),
(17, 17, 'Pham Thi Dung', '0901000004', '34 Kim Ma, Giang Vo, Ba Dinh, Ha Noi', 'TRK-17', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-05 14:30:00', NULL, '2024-07-05 14:30:00', '2024-07-05 14:30:00'),
(18, 18, 'Pham Thi Dung', '0901000004', '34 Kim Ma, Giang Vo, Ba Dinh, Ha Noi', 'TRK-18', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-10 09:00:00', NULL, '2024-07-10 09:00:00', '2024-07-10 09:00:00'),
(19, 19, 'Pham Thi Dung', '0901000004', '34 Kim Ma, Giang Vo, Ba Dinh, Ha Noi', 'TRK-19', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-15 11:30:00', NULL, '2024-07-15 11:30:00', '2024-07-15 11:30:00'),
(20, 20, 'Pham Thi Dung', '0901000004', '34 Kim Ma, Giang Vo, Ba Dinh, Ha Noi', 'TRK-20', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-20 15:00:00', NULL, '2024-07-20 15:00:00', '2024-07-20 15:00:00'),
(21, 21, 'Hoang Van Em', '0901000005', '56 Bach Dang, Hai Chau 1, Hai Chau, Da Nang', 'TRK-21', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-02 10:30:00', NULL, '2024-07-02 10:30:00', '2024-07-02 10:30:00'),
(22, 22, 'Hoang Van Em', '0901000005', '56 Bach Dang, Hai Chau 1, Hai Chau, Da Nang', 'TRK-22', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-08 13:00:00', NULL, '2024-07-08 13:00:00', '2024-07-08 13:00:00'),
(23, 23, 'Hoang Van Em', '0901000005', '56 Bach Dang, Hai Chau 1, Hai Chau, Da Nang', 'TRK-23', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-12 09:45:00', NULL, '2024-07-12 09:45:00', '2024-07-12 09:45:00'),
(24, 24, 'Hoang Van Em', '0901000005', '56 Bach Dang, Hai Chau 1, Hai Chau, Da Nang', 'TRK-24', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-18 16:30:00', NULL, '2024-07-18 16:30:00', '2024-07-18 16:30:00'),
(25, 25, 'Hoang Van Em', '0901000005', '56 Bach Dang, Hai Chau 1, Hai Chau, Da Nang', 'TRK-25', 'FastDelivery', 0.00, 'SHIPPING', '2024-07-22 11:00:00', NULL, '2024-07-22 11:00:00', '2024-07-22 11:00:00'),
(26, 26, 'Vo Thi Fang', '0901000006', '78 Nguyen Trai, Phu Hoi, Hue, Thua Thien Hue', 'TRK-26', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-01 10:00:00', '2024-08-01 10:00:00'),
(27, 27, 'Vo Thi Fang', '0901000006', '78 Nguyen Trai, Phu Hoi, Hue, Thua Thien Hue', 'TRK-27', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-05 14:30:00', '2024-08-05 14:30:00'),
(28, 28, 'Vo Thi Fang', '0901000006', '78 Nguyen Trai, Phu Hoi, Hue, Thua Thien Hue', 'TRK-28', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-10 09:15:00', '2024-08-10 09:15:00'),
(29, 29, 'Vo Thi Fang', '0901000006', '78 Nguyen Trai, Phu Hoi, Hue, Thua Thien Hue', 'TRK-29', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-15 11:00:00', '2024-08-15 11:00:00'),
(30, 30, 'Vo Thi Fang', '0901000006', '78 Nguyen Trai, Phu Hoi, Hue, Thua Thien Hue', 'TRK-30', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-20 15:30:00', '2024-08-20 15:30:00'),
(31, 31, 'Dang Van Giang', '0901000007', '90 Le Duan, Thach Thang, Hai Chau, Da Nang', 'TRK-31', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-02 10:30:00', '2024-08-02 10:30:00'),
(32, 32, 'Dang Van Giang', '0901000007', '90 Le Duan, Thach Thang, Hai Chau, Da Nang', 'TRK-32', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-08 13:00:00', '2024-08-08 13:00:00'),
(33, 33, 'Dang Van Giang', '0901000007', '90 Le Duan, Thach Thang, Hai Chau, Da Nang', 'TRK-33', 'FastDelivery', 0.00, 'READY_TO_SHIP', NULL, NULL, '2024-08-12 09:45:00', '2024-08-12 09:45:00'),
(34, 34, 'Dang Van Giang', '0901000007', '90 Le Duan, Thach Thang, Hai Chau, Da Nang', 'TRK-34', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-08-18 16:30:00', '2024-08-18 16:30:00'),
(35, 35, 'Dang Van Giang', '0901000007', '90 Le Duan, Thach Thang, Hai Chau, Da Nang', 'TRK-35', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-08-22 11:00:00', '2024-08-22 11:00:00'),
(36, 36, 'Bui Thi Hoa', '0901000008', '111 Vo Van Kiet, Cau Ong Lanh, Quan 1, Ho Chi Minh', 'TRK-36', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-09-01 10:00:00', '2024-09-01 10:00:00'),
(37, 37, 'Bui Thi Hoa', '0901000008', '111 Vo Van Kiet, Cau Ong Lanh, Quan 1, Ho Chi Minh', 'TRK-37', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-09-05 14:30:00', '2024-09-05 14:30:00'),
(38, 38, 'Bui Thi Hoa', '0901000008', '111 Vo Van Kiet, Cau Ong Lanh, Quan 1, Ho Chi Minh', 'TRK-38', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-09-10 09:15:00', '2024-09-10 09:15:00'),
(39, 39, 'Bui Thi Hoa', '0901000008', '111 Vo Van Kiet, Cau Ong Lanh, Quan 1, Ho Chi Minh', 'TRK-39', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-09-15 11:00:00', '2024-09-15 11:00:00'),
(40, 40, 'Bui Thi Hoa', '0901000008', '111 Vo Van Kiet, Cau Ong Lanh, Quan 1, Ho Chi Minh', 'TRK-40', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-09-20 15:30:00', '2024-09-20 15:30:00'),
(41, 41, 'Ngo Van Ich', '0901000009', '222 Nguyen Van Linh, An Khanh, Ninh Kieu, Can Tho', 'TRK-41', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-09-25 10:30:00', '2024-09-25 10:30:00'),
(42, 42, 'Ngo Van Ich', '0901000009', '222 Nguyen Van Linh, An Khanh, Ninh Kieu, Can Tho', 'TRK-42', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-10-01 13:00:00', '2024-10-01 13:00:00'),
(43, 43, 'Ngo Van Ich', '0901000009', '222 Nguyen Van Linh, An Khanh, Ninh Kieu, Can Tho', 'TRK-43', 'FastDelivery', 0.00, 'PENDING', NULL, NULL, '2024-10-05 09:45:00', '2024-10-05 09:45:00'),
(44, 44, 'Ngo Van Ich', '0901000009', '222 Nguyen Van Linh, An Khanh, Ninh Kieu, Can Tho', 'TRK-44', 'FastDelivery', 0.00, 'FAILED', NULL, NULL, '2024-10-10 16:30:00', '2024-10-10 16:30:00'),
(45, 45, 'Ngo Van Ich', '0901000009', '222 Nguyen Van Linh, An Khanh, Ninh Kieu, Can Tho', 'TRK-45', 'FastDelivery', 0.00, 'FAILED', NULL, NULL, '2024-10-15 11:00:00', '2024-10-15 11:00:00'),
(46, 46, 'Ly Thi Kim', '0901000010', '333 Tran Phu, Loc Tho, Nha Trang, Khanh Hoa', 'TRK-46', 'FastDelivery', 0.00, 'FAILED', NULL, NULL, '2024-10-20 10:00:00', '2024-10-20 10:00:00'),
(47, 47, 'Ly Thi Kim', '0901000010', '333 Tran Phu, Loc Tho, Nha Trang, Khanh Hoa', 'TRK-47', 'FastDelivery', 0.00, 'FAILED', NULL, NULL, '2024-10-25 14:30:00', '2024-10-25 14:30:00'),
(48, 48, 'Ly Thi Kim', '0901000010', '333 Tran Phu, Loc Tho, Nha Trang, Khanh Hoa', 'TRK-48', 'FastDelivery', 0.00, 'FAILED', NULL, NULL, '2024-11-01 09:15:00', '2024-11-01 09:15:00'),
(49, 49, 'Ly Thi Kim', '0901000010', '333 Tran Phu, Loc Tho, Nha Trang, Khanh Hoa', 'TRK-49', 'FastDelivery', 0.00, 'RETURNED', '2024-11-05 11:00:00', '2024-11-05 11:00:00', '2024-11-05 11:00:00', '2024-11-05 11:00:00'),
(50, 50, 'Ly Thi Kim', '0901000010', '333 Tran Phu, Loc Tho, Nha Trang, Khanh Hoa', 'TRK-50', 'FastDelivery', 0.00, 'RETURNED', '2024-11-10 15:30:00', '2024-11-10 15:30:00', '2024-11-10 15:30:00', '2024-11-10 15:30:00');

-- ----------------------------------------------------------------
-- Reviews (20 reviews from users with DELIVERED orders)
-- ----------------------------------------------------------------
INSERT INTO reviews (id, user_id, product_id, rating, comment, status) VALUES
(1,  2, 1,  5, 'Excellent laptop! The M3 chip is incredibly fast and the battery life is amazing. Perfect for daily use.',                       'APPROVED'),
(2,  2, 3,  4, 'Great MacBook Pro with stunning display. Slightly heavy for travel but performance is top-notch.',                                'APPROVED'),
(3,  2, 6,  5, 'The Dell XPS 13 Plus has a gorgeous design. The edge-to-edge keyboard takes some getting used to but I love it.',                'APPROVED'),
(4,  2, 7,  4, 'Solid laptop for creative work. The OLED display is breathtaking. Wish the battery lasted a bit longer.',                         'APPROVED'),
(5,  2, 9,  3, 'Decent budget laptop for students. Good keyboard and display but the plastic build feels a bit cheap.',                            'APPROVED'),
(6,  3, 11, 5, 'Outstanding workstation! Handles all my CAD and rendering tasks without breaking a sweat.',                                        'APPROVED'),
(7,  3, 13, 4, 'The HP Envy 16 is a great content creation laptop. Beautiful OLED display and good performance.',                                  'APPROVED'),
(8,  3, 14, 3, 'Basic laptop that gets the job done. Good for browsing and office work but not much else.',                                        'APPROVED'),
(9,  3, 17, 5, 'The ThinkPad X1 Carbon is the best business laptop I have ever used. Lightweight, durable, and a fantastic keyboard.',             'APPROVED'),
(10, 3, 19, 4, 'Great value for money. The IdeaPad 5 Pro offers solid performance and a beautiful 2.5K display at a reasonable price.',            'APPROVED'),
(11, 4, 21, 5, 'The Yoga 9i is a premium convertible done right. The OLED display and soundbar speakers are incredible.',                           'APPROVED'),
(12, 4, 23, 4, 'Excellent gaming laptop. The 240Hz display is buttery smooth and the RTX 4070 handles every game I throw at it.',                   'APPROVED'),
(13, 4, 24, 5, 'The ZenBook 14 OLED is simply gorgeous. One of the best ultrabooks on the market right now.',                                       'APPROVED'),
(14, 4, 25, 3, 'A solid budget option. Nothing fancy but reliable for everyday computing tasks. Good enough for students.',                         'APPROVED'),
(15, 4, 27, 5, 'ProArt Studiobook is a beast for video editing. The ASUS Dial is incredibly useful for creative workflows.',                        'APPROVED'),
(16, 4, 29, 4, 'The Predator Helios 16 is a fantastic gaming machine. Incredible cooling system keeps temperatures in check.',                      'APPROVED'),
(17, 2, 2,  5, 'Love the 15-inch MacBook Air. The larger screen makes a huge difference for productivity. Great laptop overall.',                   'APPROVED'),
(18, 3, 15, 4, 'HP EliteBook 840 is an excellent business laptop. Great security features and comfortable keyboard.',                               'APPROVED'),
(19, 4, 28, 5, 'The ROG Zephyrus G14 is the perfect balance of portability and gaming performance. Highly recommended.',                            'APPROVED'),
(20, 3, 12, 4, 'HP Spectre x360 is a beautiful machine. The OLED display in tablet mode is amazing for media consumption.',                         'PENDING');

-- ----------------------------------------------------------------
-- Notifications (~20 notifications)
-- ----------------------------------------------------------------
INSERT INTO notifications (id, user_id, title, message, type, is_read, reference_id, created_at) VALUES
(1,  2,  'Order Delivered',       'Your order ORD-2024-00001 has been delivered successfully.',              'ORDER',     TRUE,  'ORD-2024-00001', '2024-01-20 14:05:00'),
(2,  2,  'Payment Confirmed',     'Payment of $4,098.00 for order ORD-2024-00002 has been confirmed.',     'PAYMENT',   TRUE,  'ORD-2024-00002', '2024-02-15 10:35:00'),
(3,  2,  'Order Delivered',       'Your order ORD-2024-00003 has been delivered successfully.',              'ORDER',     TRUE,  'ORD-2024-00003', '2024-03-10 16:05:00'),
(4,  3,  'Welcome!',              'Welcome to Laptop Store! Start browsing our collection of premium laptops.', 'SYSTEM', TRUE,  NULL,              '2024-01-01 00:00:00'),
(5,  3,  'Order Delivered',       'Your order ORD-2024-00006 has been delivered successfully.',              'ORDER',     TRUE,  'ORD-2024-00006', '2024-01-27 15:05:00'),
(6,  4,  'Order Delivered',       'Your order ORD-2024-00011 has been delivered successfully.',              'ORDER',     TRUE,  'ORD-2024-00011', '2024-02-10 10:35:00'),
(7,  5,  'Flash Sale!',           'Flash Sale starts now! Up to 30% off on selected gaming laptops.',       'PROMOTION', FALSE, NULL,              '2024-07-01 00:00:00'),
(8,  5,  'Order Shipped',         'Your order ORD-2024-00016 has been shipped. Track: VN100000016.',        'ORDER',     FALSE, 'ORD-2024-00016', '2024-07-03 08:05:00'),
(9,  6,  'Order Shipped',         'Your order ORD-2024-00021 has been shipped. Track: VN100000021.',        'ORDER',     FALSE, 'ORD-2024-00021', '2024-07-04 08:05:00'),
(10, 7,  'Order Processing',      'Your order ORD-2024-00026 is being processed.',                          'ORDER',     FALSE, 'ORD-2024-00026', '2024-08-02 10:05:00'),
(11, 8,  'Order Confirmed',       'Your order ORD-2024-00034 has been confirmed.',                           'ORDER',     FALSE, 'ORD-2024-00034', '2024-08-18 16:35:00'),
(12, 9,  'Payment Confirmed',     'Payment of $4,843.10 for order ORD-2024-00036 has been confirmed.',     'PAYMENT',   FALSE, 'ORD-2024-00036', '2024-09-03 10:05:00'),
(13, 10, 'Order Cancelled',       'Your order ORD-2024-00044 has been cancelled. Refund is being processed.', 'ORDER',  TRUE,  'ORD-2024-00044', '2024-10-11 10:00:00'),
(14, 11, 'Refund Processed',      'Refund of $3,298.00 for order ORD-2024-00049 has been processed.',      'PAYMENT',   FALSE, 'ORD-2024-00049', '2024-11-08 11:00:00'),
(15, 2,  'Summer Sale!',          'Dont miss our Summer Sale! Use code SUMMER20 for 20% off.',              'PROMOTION', TRUE,  NULL,              '2024-06-01 00:00:00'),
(16, 3,  'New Arrivals',          'Check out the latest laptops from Apple and Dell. Now available!',        'PROMOTION', FALSE, NULL,              '2024-05-01 00:00:00'),
(17, 4,  'Back to School',        'Back to School Sale! Use code BACKTOSCHOOL for 15% off.',                'PROMOTION', TRUE,  NULL,              '2024-08-01 00:00:00'),
(18, 5,  'System Maintenance',    'Scheduled maintenance on Dec 15. The store will be briefly unavailable.', 'SYSTEM',   FALSE, NULL,              '2024-12-10 00:00:00'),
(19, 6,  'Welcome!',              'Welcome to Laptop Store! Explore the best laptops at great prices.',     'SYSTEM',    TRUE,  NULL,              '2024-01-01 00:00:00'),
(20, 7,  'Welcome!',              'Welcome to Laptop Store! Start your laptop shopping journey today.',     'SYSTEM',    TRUE,  NULL,              '2024-01-01 00:00:00');

-- ----------------------------------------------------------------
-- Inventory (50 entries, one per product)
-- ----------------------------------------------------------------
INSERT INTO inventory (id, product_id, quantity, reserved_quantity, last_restocked_at) VALUES
(1,  1,  50, 3,  '2024-06-01 08:00:00'),
(2,  2,  40, 2,  '2024-06-01 08:00:00'),
(3,  3,  35, 1,  '2024-06-01 08:00:00'),
(4,  4,  25, 2,  '2024-05-15 08:00:00'),
(5,  5,  15, 1,  '2024-05-15 08:00:00'),
(6,  6,  30, 2,  '2024-06-10 08:00:00'),
(7,  7,  25, 1,  '2024-06-10 08:00:00'),
(8,  8,  20, 0,  '2024-06-10 08:00:00'),
(9,  9,  60, 3,  '2024-07-01 08:00:00'),
(10, 10, 30, 1,  '2024-06-15 08:00:00'),
(11, 11, 15, 0,  '2024-05-01 08:00:00'),
(12, 12, 25, 2,  '2024-06-20 08:00:00'),
(13, 13, 20, 1,  '2024-06-20 08:00:00'),
(14, 14, 70, 5,  '2024-07-01 08:00:00'),
(15, 15, 20, 0,  '2024-05-01 08:00:00'),
(16, 16, 10, 0,  '2024-04-15 08:00:00'),
(17, 17, 25, 1,  '2024-06-01 08:00:00'),
(18, 18, 30, 2,  '2024-06-01 08:00:00'),
(19, 19, 45, 3,  '2024-07-01 08:00:00'),
(20, 20, 20, 2,  '2024-06-15 08:00:00'),
(21, 21, 20, 1,  '2024-06-15 08:00:00'),
(22, 22, 15, 0,  '2024-05-01 08:00:00'),
(23, 23, 25, 2,  '2024-06-20 08:00:00'),
(24, 24, 35, 2,  '2024-07-01 08:00:00'),
(25, 25, 80, 5,  '2024-07-15 08:00:00'),
(26, 26, 40, 3,  '2024-07-01 08:00:00'),
(27, 27, 15, 0,  '2024-05-01 08:00:00'),
(28, 28, 20, 1,  '2024-06-15 08:00:00'),
(29, 29, 18, 1,  '2024-06-01 08:00:00'),
(30, 30, 35, 2,  '2024-07-01 08:00:00'),
(31, 31, 65, 4,  '2024-07-15 08:00:00'),
(32, 32, 45, 3,  '2024-07-01 08:00:00'),
(33, 33, 10, 0,  '2024-04-01 08:00:00'),
(34, 34, 10, 1,  '2024-05-15 08:00:00'),
(35, 35, 20, 1,  '2024-06-15 08:00:00'),
(36, 36, 30, 2,  '2024-07-01 08:00:00'),
(37, 37, 12, 0,  '2024-05-01 08:00:00'),
(38, 38, 50, 3,  '2024-07-15 08:00:00'),
(39, 39, 15, 0,  '2024-05-15 08:00:00'),
(40, 40, 12, 1,  '2024-05-01 08:00:00'),
(41, 41, 40, 2,  '2024-07-01 08:00:00'),
(42, 42, 30, 1,  '2024-06-15 08:00:00'),
(43, 43, 20, 1,  '2024-06-01 08:00:00'),
(44, 44, 40, 2,  '2024-07-01 08:00:00'),
(45, 45, 25, 1,  '2024-06-15 08:00:00'),
(46, 46, 20, 0,  '2024-06-01 08:00:00'),
(47, 47, 8,  1,  '2024-04-15 08:00:00'),
(48, 48, 12, 0,  '2024-05-01 08:00:00'),
(49, 49, 15, 1,  '2024-05-15 08:00:00'),
(50, 50, 18, 0,  '2024-06-01 08:00:00');

-- ----------------------------------------------------------------
-- Flash Sales (5 flash sales)
-- ----------------------------------------------------------------
INSERT INTO flash_sales (id, product_id, sale_price, stock, sold, start_time, end_time, status) VALUES
(1, 26, 899.00,  20, 12, '2024-07-01 00:00:00', '2024-07-03 23:59:59', 'ENDED'),
(2, 32, 649.00,  15, 8,  '2024-07-15 00:00:00', '2024-07-17 23:59:59', 'ENDED'),
(3, 25, 349.00,  30, 18, '2024-08-01 00:00:00', '2024-08-03 23:59:59', 'ENDED'),
(4, 38, 549.00,  25, 0,  '2025-01-01 00:00:00', '2025-01-03 23:59:59', 'UPCOMING'),
(5, 14, 399.00,  20, 0,  '2025-02-01 00:00:00', '2025-02-03 23:59:59', 'UPCOMING');

-- ----------------------------------------------------------------
-- Refresh Tokens (sample tokens)
-- ----------------------------------------------------------------
INSERT INTO refresh_tokens (id, user_id, token, expiry_date, revoked) VALUES
(1, 1,  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh.admin-token-001',  '2025-12-31 23:59:59', FALSE),
(2, 2,  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh.user01-token-001', '2025-12-31 23:59:59', FALSE),
(3, 3,  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.refresh.user02-token-001', '2025-12-31 23:59:59', FALSE);

-- ----------------------------------------------------------------
-- Audit Logs (10 sample entries)
-- ----------------------------------------------------------------
INSERT INTO audit_logs (id, user_id, action, entity_type, entity_id, old_value, new_value, ip_address, created_at) VALUES
(1,  1, 'CREATE', 'PRODUCT',  1,  NULL,                                      '{"name":"MacBook Air M3 13 inch","price":1099.00}',     '192.168.1.1', '2024-01-01 08:00:00'),
(2,  1, 'CREATE', 'PRODUCT',  20, NULL,                                      '{"name":"Lenovo Legion Pro 5 16","price":1899.00}',      '192.168.1.1', '2024-01-01 08:15:00'),
(3,  1, 'UPDATE', 'PRODUCT',  2,  '{"discount_price":null}',                  '{"discount_price":1199.00}',                            '192.168.1.1', '2024-03-01 10:00:00'),
(4,  1, 'CREATE', 'COUPON',   1,  NULL,                                      '{"code":"WELCOME10","discount_type":"PERCENTAGE"}',      '192.168.1.1', '2024-01-01 09:00:00'),
(5,  1, 'UPDATE', 'ORDER',    1,  '{"status":"SHIPPING"}',                    '{"status":"DELIVERED"}',                                '192.168.1.1', '2024-01-20 14:00:00'),
(6,  1, 'UPDATE', 'ORDER',    44, '{"status":"CONFIRMED"}',                   '{"status":"CANCELLED"}',                                '192.168.1.1', '2024-10-11 10:00:00'),
(7,  1, 'CREATE', 'BRAND',    1,  NULL,                                      '{"name":"Apple","slug":"apple"}',                        '192.168.1.1', '2024-01-01 07:00:00'),
(8,  1, 'UPDATE', 'INVENTORY',1,  '{"quantity":30}',                          '{"quantity":50}',                                       '192.168.1.1', '2024-06-01 08:00:00'),
(9,  1, 'CREATE', 'FLASH_SALE',1, NULL,                                      '{"product_id":26,"sale_price":899.00}',                  '192.168.1.1', '2024-06-28 10:00:00'),
(10, 1, 'UPDATE', 'USER',     2,  '{"phone":"0900000001"}',                   '{"phone":"0901000001"}',                                '192.168.1.1', '2024-02-01 10:00:00');

-- ================================================================
-- END OF SCRIPT
-- ================================================================
