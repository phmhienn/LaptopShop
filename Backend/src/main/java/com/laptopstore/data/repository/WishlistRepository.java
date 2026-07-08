package com.laptopstore.data.repository;

import com.laptopstore.data.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);

    Optional<Wishlist> findFirstByUserId(Long userId);

    boolean existsByUserIdAndId(Long userId, Long id);
}
