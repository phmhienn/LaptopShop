package com.laptopstore.data.repository;

import com.laptopstore.data.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    boolean existsByCartIdAndProductId(Long cartId, Long productId);

    void deleteByCartId(Long cartId);

    long countByCartId(Long cartId);
}
