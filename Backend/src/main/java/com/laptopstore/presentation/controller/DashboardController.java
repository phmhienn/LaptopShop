package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.other.DashboardStatsDTO;
import com.laptopstore.application.facade.DashboardFacade;
import com.laptopstore.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardFacade dashboardFacade;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getDashboardStats() {
        DashboardStatsDTO stats = dashboardFacade.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
