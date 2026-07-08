package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.other.CouponDTO;
import com.laptopstore.application.facade.CouponFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponFacade couponFacade;

    // Public Endpoint
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal orderAmount) {
        boolean isValid = couponFacade.validateCoupon(code, orderAmount);
        return ResponseEntity.ok(ApiResponse.success("Coupon is valid", isValid));
    }

    // Admin Endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<CouponDTO>>> getAllCoupons(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<CouponDTO> response = couponFacade.searchCoupons(keyword, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDTO>> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(couponFacade.getCouponById(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDTO>> createCoupon(@RequestBody CouponDTO couponDTO) {
        CouponDTO createdCoupon = couponFacade.createCoupon(couponDTO);
        return ResponseEntity.ok(ApiResponse.success("Coupon created successfully", createdCoupon));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CouponDTO>> updateCoupon(@PathVariable Long id, @RequestBody CouponDTO couponDTO) {
        CouponDTO updatedCoupon = couponFacade.updateCoupon(id, couponDTO);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated successfully", updatedCoupon));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCoupon(@PathVariable Long id) {
        couponFacade.deleteCoupon(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> toggleCouponStatus(@PathVariable Long id) {
        couponFacade.toggleCouponStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon status toggled successfully"));
    }
}
