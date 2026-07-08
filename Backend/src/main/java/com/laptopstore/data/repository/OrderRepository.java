package com.laptopstore.data.repository;

import com.laptopstore.common.enums.ShipmentStatus;
import com.laptopstore.data.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    long countByCreatedAtAfter(LocalDateTime date);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi WHERE o.user.id = :userId AND oi.product.id = :productId AND o.shipment.status = 'DELIVERED'")
    boolean hasUserPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.shipment.status = :status")
    Page<Order> findByUserIdAndShipmentStatus(@Param("userId") Long userId, @Param("status") ShipmentStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.shipment.status = :status")
    Page<Order> findByShipmentStatus(@Param("status") ShipmentStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Page<Order> findByDateRange(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end,
                                 Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shipment.status = :status")
    long countByShipmentStatus(@Param("status") ShipmentStatus status);

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.shipment.status = 'DELIVERED'")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(o.finalAmount), 0) FROM Order o WHERE o.shipment.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end")
    BigDecimal calculateRevenueByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    long countOrdersByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o.shipment.status, COUNT(o) FROM Order o GROUP BY o.shipment.status")
    List<Object[]> countOrdersByShipmentStatus();

    @Query("SELECT FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m'), COALESCE(SUM(o.finalAmount), 0), COUNT(o) " +
            "FROM Order o WHERE o.shipment.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end " +
            "GROUP BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m') ORDER BY FUNCTION('DATE_FORMAT', o.createdAt, '%Y-%m')")
    List<Object[]> getMonthlyRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.orderCode LIKE CONCAT('%', :keyword, '%') OR " +
            "o.shipment.receiverName LIKE CONCAT('%', :keyword, '%')")
    Page<Order> searchOrders(@Param("keyword") String keyword, Pageable pageable);
}
