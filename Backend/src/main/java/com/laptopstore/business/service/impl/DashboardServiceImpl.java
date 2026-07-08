package com.laptopstore.business.service.impl;

import com.laptopstore.business.service.DashboardService;
import com.laptopstore.data.repository.OrderRepository;
import com.laptopstore.data.repository.ProductRepository;
import com.laptopstore.data.repository.ReviewRepository;
import com.laptopstore.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "'totalUsers'")
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "'totalProducts'")
    public long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "'totalOrders'")
    public long getTotalOrders() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "'totalRevenue'")
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long getNewOrdersCount(LocalDateTime since) {
        // Không cache — kết quả phụ thuộc vào tham số `since` động
        return orderRepository.countByCreatedAtAfter(since);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "'pendingReviews'")
    public long getPendingReviewsCount() {
        return reviewRepository.countPendingReviews();
    }
}
