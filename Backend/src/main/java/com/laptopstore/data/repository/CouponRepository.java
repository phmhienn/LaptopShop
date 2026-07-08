package com.laptopstore.data.repository;

import com.laptopstore.common.enums.CouponStatus;
import com.laptopstore.data.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCode(String code);

    boolean existsByCode(String code);

    Page<Coupon> findByStatus(CouponStatus status, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Coupon c WHERE LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Coupon> searchCoupons(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
}
