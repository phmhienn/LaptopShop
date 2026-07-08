package com.laptopstore.application.facade;

import com.laptopstore.application.dto.other.CouponDTO;
import com.laptopstore.application.mapper.CouponMapper;
import com.laptopstore.business.service.CouponService;
import com.laptopstore.common.enums.DiscountType;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponFacade {

    private final CouponService couponService;
    private final CouponMapper couponMapper;

    @Transactional
    public CouponDTO createCoupon(CouponDTO requestDTO) {
        Coupon coupon = new Coupon();
        mapDTOToEntity(requestDTO, coupon);
        return couponMapper.toCouponDTO(couponService.createCoupon(coupon));
    }

    @Transactional
    public CouponDTO updateCoupon(Long id, CouponDTO requestDTO) {
        Coupon coupon = new Coupon();
        mapDTOToEntity(requestDTO, coupon);
        return couponMapper.toCouponDTO(couponService.updateCoupon(id, coupon));
    }

    public CouponDTO getCouponById(Long id) {
        return couponMapper.toCouponDTO(couponService.getCouponById(id));
    }

    public CouponDTO getCouponByCode(String code) {
        return couponMapper.toCouponDTO(couponService.getCouponByCode(code));
    }

    @Transactional
    public void deleteCoupon(Long id) {
        couponService.deleteCoupon(id);
    }

    @Transactional
    public void toggleCouponStatus(Long id) {
        couponService.toggleCouponStatus(id);
    }

    public PagedResponse<CouponDTO> searchCoupons(String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Coupon> couponsPage = couponService.searchCoupons(keyword, pageable);

        List<CouponDTO> content = couponsPage.getContent().stream()
                .map(couponMapper::toCouponDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, couponsPage.getNumber(), couponsPage.getSize(),
                couponsPage.getTotalElements(), couponsPage.getTotalPages());
    }

    public boolean validateCoupon(String code, BigDecimal orderAmount) {
        return couponService.validateCoupon(code, orderAmount);
    }
    
    private void mapDTOToEntity(CouponDTO dto, Coupon entity) {
        entity.setCode(dto.getCode());
        entity.setDescription(dto.getDescription());
        if (dto.getDiscountType() != null) {
            entity.setDiscountType(DiscountType.valueOf(dto.getDiscountType().toUpperCase()));
        }
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setMinOrderAmount(dto.getMinOrderAmount());
        entity.setMaxDiscountAmount(dto.getMaxDiscountAmount());
        entity.setUsageLimit(dto.getUsageLimit());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
    }
}
