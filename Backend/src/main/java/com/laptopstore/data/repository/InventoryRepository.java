package com.laptopstore.data.repository;

import com.laptopstore.data.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    // Batch load inventories theo list productId — tránh N query trong loop
    @Query("SELECT i FROM Inventory i LEFT JOIN FETCH i.product WHERE i.product.id IN :productIds")
    List<Inventory> findByProductIdIn(@Param("productIds") Collection<Long> productIds);

    @Query("SELECT i FROM Inventory i WHERE i.quantity - i.reservedQuantity <= :threshold")
    List<Inventory> findLowStockItems(@Param("threshold") int threshold);

    @Query("SELECT i FROM Inventory i WHERE i.quantity = 0")
    List<Inventory> findOutOfStockItems();
}
