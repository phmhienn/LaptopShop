package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.CouponService;
import com.laptopstore.common.enums.CouponStatus;
import com.laptopstore.data.entity.Coupon;
import com.laptopstore.data.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    @Transactional
    public Coupon createCoupon(Coupon coupon) {
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new ValidationException("Coupon code already exists");
        }
        
        if (coupon.getEndDate() != null && coupon.getStartDate() != null && 
            coupon.getEndDate().isBefore(coupon.getStartDate())) {
            throw new ValidationException("End date must be after start date");
        }
        
        coupon.setCode(coupon.getCode().toUpperCase());
        coupon.setUsedCount(0);
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public Coupon updateCoupon(Long id, Coupon couponDetails) {
        Coupon coupon = getCouponById(id);
        
        if (!coupon.getCode().equalsIgnoreCase(couponDetails.getCode()) && 
            couponRepository.existsByCode(couponDetails.getCode())) {
            throw new ValidationException("Coupon code already exists");
        }
        
        coupon.setCode(couponDetails.getCode().toUpperCase());
        coupon.setDescription(couponDetails.getDescription());
        coupon.setDiscountType(couponDetails.getDiscountType());
        coupon.setDiscountValue(couponDetails.getDiscountValue());
        coupon.setMinOrderAmount(couponDetails.getMinOrderAmount());
        coupon.setMaxDiscountAmount(couponDetails.getMaxDiscountAmount());
        coupon.setUsageLimit(couponDetails.getUsageLimit());
        coupon.setStartDate(couponDetails.getStartDate());
        coupon.setEndDate(couponDetails.getEndDate());
        
        return couponRepository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponById(Long id) {
        return couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", id));
    }

    @Override
    @Transactional(readOnly = true)
    public Coupon getCouponByCode(String code) {
        return couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "code", code));
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        Coupon coupon = getCouponById(id);
        if (coupon.getUsedCount() > 0) {
            throw new ValidationException("Cannot delete coupon that has already been used. Disable it instead.");
        }
        couponRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleCouponStatus(Long id) {
        Coupon coupon = getCouponById(id);
        if (coupon.getStatus() == CouponStatus.ACTIVE) {
            coupon.setStatus(CouponStatus.INACTIVE);
        } else {
            coupon.setStatus(CouponStatus.ACTIVE);
        }
        couponRepository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Coupon> searchCoupons(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return couponRepository.findAll(pageable);
        }
        return couponRepository.searchCoupons(keyword.trim(), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateCoupon(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ValidationException("Invalid coupon code"));
                
        if (!coupon.isValid()) {
            throw new ValidationException("Coupon is expired, inactive, or usage limit reached");
        }
        
        if (coupon.getMinOrderAmount() != null && orderAmount.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new ValidationException("Order amount does not meet the minimum requirement for this coupon");
        }
        
        return true;
    }
}
