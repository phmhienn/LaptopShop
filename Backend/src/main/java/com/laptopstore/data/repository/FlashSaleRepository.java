package com.laptopstore.data.repository;

import com.laptopstore.common.enums.FlashSaleStatus;
import com.laptopstore.data.entity.FlashSale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    Page<FlashSale> findByStatus(FlashSaleStatus status, Pageable pageable);

    @Query("SELECT fs FROM FlashSale fs WHERE fs.status = 'ACTIVE' AND fs.sold < fs.stock " +
            "AND CURRENT_TIMESTAMP BETWEEN fs.startTime AND fs.endTime")
    List<FlashSale> findActiveFlashSales();

    List<FlashSale> findByProductId(Long productId);
}
