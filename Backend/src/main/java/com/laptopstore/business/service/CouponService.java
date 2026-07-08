package com.laptopstore.business.service;

import com.laptopstore.data.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CouponService {
    
    Coupon createCoupon(Coupon coupon);
    
    Coupon updateCoupon(Long id, Coupon couponDetails);
    
    Coupon getCouponById(Long id);
    
    Coupon getCouponByCode(String code);
    
    void deleteCoupon(Long id);
    
    void toggleCouponStatus(Long id);
    
    Page<Coupon> searchCoupons(String keyword, Pageable pageable);
    
    boolean validateCoupon(String code, java.math.BigDecimal orderAmount);
}
