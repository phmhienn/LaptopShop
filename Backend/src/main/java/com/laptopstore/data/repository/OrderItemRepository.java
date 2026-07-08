package com.laptopstore.data.repository;

import com.laptopstore.data.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("SELECT oi.productName, SUM(oi.quantity) as totalSold, SUM(oi.subtotal) as totalRevenue " +
            "FROM OrderItem oi JOIN oi.order o WHERE o.shipment.status = 'DELIVERED' " +
            "GROUP BY oi.productName ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts();

    @Query("SELECT oi.productName, SUM(oi.quantity) as totalSold, SUM(oi.subtotal) as totalRevenue " +
            "FROM OrderItem oi JOIN oi.order o WHERE o.shipment.status = 'DELIVERED' AND o.createdAt BETWEEN :start AND :end " +
            "GROUP BY oi.productName ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProductsByDateRange(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);
}
