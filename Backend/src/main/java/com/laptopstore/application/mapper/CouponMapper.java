package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.other.CouponDTO;
import com.laptopstore.data.entity.Coupon;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public CouponDTO toCouponDTO(Coupon coupon) {
        if (coupon == null) {
            return null;
        }

        CouponDTO dto = new CouponDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDescription(coupon.getDescription());
        dto.setDiscountType(coupon.getDiscountType().name());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setMinOrderAmount(coupon.getMinOrderAmount());
        dto.setMaxDiscountAmount(coupon.getMaxDiscountAmount());
        dto.setUsageLimit(coupon.getUsageLimit());
        dto.setUsedCount(coupon.getUsedCount());
        dto.setStartDate(coupon.getStartDate());
        dto.setEndDate(coupon.getEndDate());
        dto.setStatus(coupon.getStatus().name());
        dto.setIsValid(coupon.isValid());

        return dto;
    }
}
