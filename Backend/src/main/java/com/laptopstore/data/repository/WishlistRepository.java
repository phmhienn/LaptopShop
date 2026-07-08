package com.laptopstore.data.repository;

import com.laptopstore.data.entity.Wishlist;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);

    // JOIN FETCH items + product trong 1 query — loại bỏ N+1
    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items wi LEFT JOIN FETCH wi.product p WHERE w.user.id = :userId")
    Optional<Wishlist> findFirstByUserIdWithItems(@Param("userId") Long userId);

    Optional<Wishlist> findFirstByUserId(Long userId);

    boolean existsByUserIdAndId(Long userId, Long id);
}
