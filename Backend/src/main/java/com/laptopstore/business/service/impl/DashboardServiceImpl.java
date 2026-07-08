package com.laptopstore.business.service.impl;

import com.laptopstore.business.service.DashboardService;
import com.laptopstore.data.repository.OrderRepository;
import com.laptopstore.data.repository.ProductRepository;
import com.laptopstore.data.repository.ReviewRepository;
import com.laptopstore.data.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public long getTotalUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalProducts() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalOrders() {
        return orderRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = orderRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long getNewOrdersCount(LocalDateTime since) {
        return orderRepository.countByCreatedAtAfter(since);
    }

    @Override
    @Transactional(readOnly = true)
    public long getPendingReviewsCount() {
        return reviewRepository.countPendingReviews();
    }
}
