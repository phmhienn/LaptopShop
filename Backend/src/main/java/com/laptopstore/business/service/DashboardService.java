package com.laptopstore.business.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DashboardService {
    
    long getTotalUsers();
    
    long getTotalProducts();
    
    long getTotalOrders();
    
    BigDecimal getTotalRevenue();
    
    long getNewOrdersCount(LocalDateTime since);
    
    long getPendingReviewsCount();
}
