package com.laptopstore.data.repository;

import com.laptopstore.data.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    Optional<WishlistItem> findByWishlistIdAndProductId(Long wishlistId, Long productId);

    boolean existsByWishlistIdAndProductId(Long wishlistId, Long productId);

    void deleteByWishlistIdAndProductId(Long wishlistId, Long productId);

    long countByWishlistId(Long wishlistId);

    // Check trực tiếp theo userId + productId — tránh load cả Wishlist entity
    @Query("SELECT COUNT(wi) > 0 FROM WishlistItem wi WHERE wi.wishlist.user.id = :userId AND wi.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}
