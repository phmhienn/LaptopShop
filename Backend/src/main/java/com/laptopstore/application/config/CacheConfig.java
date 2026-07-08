package com.laptopstore.application.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * CacheConfig — Cấu hình Caffeine In-Memory Cache
 *
 * Chiến lược cache theo nhóm:
 *   - brands, categories: TTL 10 phút, ít thay đổi, đọc nhiều
 *   - dashboard: TTL 5 phút, metric tổng hợp tốn query, gọi từ admin
 *   - products: TTL 3 phút, thay đổi thường xuyên hơn (giá, stock)
 *
 * Caffeine được chọn thay Redis vì:
 *   - Không cần thêm infrastructure (Railway)
 *   - Latency gần như 0 (in-memory JVM)
 *   - Phù hợp cho single-instance deployment
 *
 * Thêm dependency vào build.gradle:
 *   implementation 'com.github.ben-manes.caffeine:caffeine'
 *   (Spring Boot đã quản lý version qua BOM)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(
            // Brands — ít thay đổi, rất thường xuyên được gọi (dropdown, filter)
            buildCache("brands",            300,  10, TimeUnit.MINUTES),

            // Categories — tương tự brands
            buildCache("categories",        200,  10, TimeUnit.MINUTES),

            // Dashboard metrics — gọi mỗi lần admin reload dashboard
            buildCache("dashboard",         50,   5,  TimeUnit.MINUTES),

            // Featured products — trang chủ, ít thay đổi
            // ProductServiceImpl dùng @Cacheable("featured_products")
            buildCache("featured_products", 200,  5,  TimeUnit.MINUTES)
        ));
        return manager;
    }

    /**
     * @param name       Tên cache (khớp với value trong @Cacheable)
     * @param maxSize    Số entries tối đa (LRU eviction khi vượt quá)
     * @param ttl        Time-to-live — sau thời gian này entry bị xóa kể từ lần ghi cuối
     * @param unit       Đơn vị thời gian
     */
    private CaffeineCache buildCache(String name, int maxSize, long ttl, TimeUnit unit) {
        return new CaffeineCache(name,
            Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(ttl, unit)
                .recordStats()          // Cho phép monitor hit/miss rate nếu cần
                .build()
        );
    }
}
