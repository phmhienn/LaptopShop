package com.laptopstore.application.facade;

import com.laptopstore.application.dto.other.DashboardStatsDTO;
import com.laptopstore.business.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DashboardFacade {

    private final DashboardService dashboardService;

    public DashboardStatsDTO getDashboardStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        return DashboardStatsDTO.builder()
                .totalUsers(dashboardService.getTotalUsers())
                .totalProducts(dashboardService.getTotalProducts())
                .totalOrders(dashboardService.getTotalOrders())
                .totalRevenue(dashboardService.getTotalRevenue())
                .newOrdersToday(dashboardService.getNewOrdersCount(startOfDay))
                .pendingReviews(dashboardService.getPendingReviewsCount())
                .build();
    }
}
