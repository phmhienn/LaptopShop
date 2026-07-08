-- ================================================================
-- LaptopStore — Supplemental Performance Indexes
-- Dựa trên schema thực tế từ database.sql
--
-- ĐÃ CÓ SẴN trong CREATE TABLE (KHÔNG cần tạo lại):
--   users:          idx_users_email, idx_users_username, idx_users_enabled
--   brands:         idx_brands_status
--   categories:     idx_categories_parent, idx_categories_status
--   products:       idx_products_brand, idx_products_category, idx_products_status,
--                   idx_products_featured, idx_products_price, idx_products_name,
--                   idx_products_brand_category, idx_products_status_featured
--   product_images: idx_product_images_product
--   wishlists:      idx_wishlists_user
--   wishlist_items: idx_wishlist_items_wishlist, idx_wishlist_items_product
--   cart_items:     idx_cart_items_cart, idx_cart_items_product
--   coupons:        idx_coupons_code, idx_coupons_status, idx_coupons_dates
--   orders:         idx_orders_user, idx_orders_code, idx_orders_date
--   order_items:    idx_order_items_order, idx_order_items_product
--   payments:       idx_payments_order, idx_payments_status
--   shipments:      idx_shipments_order, idx_shipments_status, idx_shipments_tracking
--   reviews:        idx_reviews_user, idx_reviews_product, idx_reviews_rating, idx_reviews_status
--   notifications:  idx_notifications_user, idx_notifications_type,
--                   idx_notifications_read, idx_notifications_created
--   refresh_tokens: idx_refresh_tokens_user, idx_refresh_tokens_token, idx_refresh_tokens_expiry
--   audit_logs:     idx_audit_logs_user, idx_audit_logs_action, idx_audit_logs_entity, idx_audit_logs_date
--
-- File này CHỈ tạo các composite index BỔ SUNG chưa có trong schema gốc.
-- An toàn khi chạy nhiều lần — dùng stored procedure kiểm tra information_schema.
-- ================================================================

USE LaptopStoreDB;

-- ================================================================
-- Helper procedure: tạo index nếu chưa tồn tại
-- ================================================================
DROP PROCEDURE IF EXISTS AddIndexIfNotExists;

DELIMITER $$
CREATE PROCEDURE AddIndexIfNotExists(
    IN p_table  VARCHAR(128),
    IN p_index  VARCHAR(128),
    IN p_cols   VARCHAR(256)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM   information_schema.statistics
        WHERE  table_schema = DATABASE()
          AND  table_name   = p_table
          AND  index_name   = p_index
    ) THEN
        SET @sql = CONCAT('CREATE INDEX `', p_index, '` ON `', p_table, '` (', p_cols, ')');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SELECT CONCAT('[OK] Created : ', p_index) AS result;
    ELSE
        SELECT CONCAT('[--] Skipped : ', p_index) AS result;
    END IF;
END$$
DELIMITER ;

-- ================================================================
-- TABLE: products — Composite indexes bổ sung cho search + filter
-- (idx_products_status, idx_products_price, idx_products_featured ĐÃ CÓ)
-- ================================================================

-- searchProducts(): WHERE status='ACTIVE' AND price BETWEEN ? AND ? ORDER BY price
-- Covering index giúp tránh filesort
CALL AddIndexIfNotExists('products', 'idx_products_status_price',
    '`status`, `price`');

-- getLatestProducts(): WHERE status='ACTIVE' ORDER BY created_at DESC
CALL AddIndexIfNotExists('products', 'idx_products_status_created',
    '`status`, `created_at` DESC');

-- getFeaturedProducts(): WHERE featured=TRUE AND status='ACTIVE' ORDER BY created_at DESC
-- Nâng cấp idx_products_status_featured có sẵn bằng thêm created_at
CALL AddIndexIfNotExists('products', 'idx_products_featured_status_created',
    '`featured`, `status`, `created_at` DESC');

-- searchProducts() với brand+category filter: WHERE brand_id=? AND category_id=? AND status='ACTIVE'
-- (idx_products_brand_category ĐÃ CÓ nhưng thiếu status)
CALL AddIndexIfNotExists('products', 'idx_products_brand_category_status',
    '`brand_id`, `category_id`, `status`');

-- ================================================================
-- TABLE: orders — Composite cho user order history + sort
-- (idx_orders_user ĐÃ CÓ nhưng thiếu created_at cho ORDER BY)
-- ================================================================

-- findByUserId() + ORDER BY created_at DESC
CALL AddIndexIfNotExists('orders', 'idx_orders_user_created',
    '`user_id`, `created_at` DESC');

-- ================================================================
-- TABLE: shipments — Composite cho admin filter theo status
-- (idx_shipments_status ĐÃ CÓ nhưng thiếu updated_at cho sort)
-- ================================================================

-- findByShipmentStatus() + ORDER BY updated_at DESC (admin dashboard)
CALL AddIndexIfNotExists('shipments', 'idx_shipments_status_updated',
    '`status`, `updated_at` DESC');

-- ================================================================
-- TABLE: reviews — Composite covering index cho product review page
-- (idx_reviews_product + idx_reviews_status ĐÃ CÓ RÀO RÃO nhưng riêng lẻ)
-- ================================================================

-- findByProductIdAndStatus() + ORDER BY created_at DESC
-- Composite (product_id, status, created_at) = Index-Only Scan cho AVG rating batch query
CALL AddIndexIfNotExists('reviews', 'idx_reviews_product_status_created',
    '`product_id`, `status`, `created_at` DESC');

-- ================================================================
-- TABLE: notifications — Composite cho unread + phân trang
-- (idx_notifications_read = (user_id, is_read) ĐÃ CÓ — thêm created_at)
-- ================================================================

-- getUnreadNotifications(): WHERE user_id=? AND is_read=FALSE ORDER BY created_at DESC
CALL AddIndexIfNotExists('notifications', 'idx_notifications_user_read_created',
    '`user_id`, `is_read`, `created_at` DESC');

-- ================================================================
-- TABLE: inventory — Index cho quantity lookup và low-stock check
-- (Không có index nào trên cột quantity trong schema gốc)
-- ================================================================

-- Tên bảng trong database.sql là "inventory" (không phải "inventories")
CALL AddIndexIfNotExists('inventory', 'idx_inventory_quantity',
    '`quantity`');

-- ================================================================
-- TABLE: refresh_tokens — Index cho cleanup job định kỳ
-- (idx_refresh_tokens_expiry ĐÃ CÓ — thêm composite với revoked)
-- ================================================================

-- DELETE expired tokens: WHERE revoked=TRUE OR expiry_date < NOW()
CALL AddIndexIfNotExists('refresh_tokens', 'idx_refresh_tokens_revoked_expiry',
    '`revoked`, `expiry_date`');

-- ================================================================
-- Dọn dẹp procedure sau khi dùng xong
-- ================================================================
DROP PROCEDURE IF EXISTS AddIndexIfNotExists;

-- ================================================================
-- Xem kết quả (bỏ comment để verify)
-- ================================================================
-- SHOW INDEX FROM products;
-- SHOW INDEX FROM orders;
-- SHOW INDEX FROM reviews;
-- SHOW INDEX FROM shipments;
-- SHOW INDEX FROM notifications;
-- SHOW INDEX FROM inventory;
