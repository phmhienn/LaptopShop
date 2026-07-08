package com.laptopstore.data.repository;

import com.laptopstore.common.enums.ReviewStatus;
import com.laptopstore.data.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // EntityGraph load user trong 1 query — tránh N+1 khi ReviewMapper dùng user.getFullName()
    @EntityGraph(attributePaths = {"user"})
    Page<Review> findByProductIdAndStatus(Long productId, ReviewStatus status, Pageable pageable);

    // EntityGraph load user + product
    @EntityGraph(attributePaths = {"user", "product"})
    Page<Review> findByUserId(Long userId, Pageable pageable);

    // Admin: cần cả user và product
    @EntityGraph(attributePaths = {"user", "product"})
    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    long countApprovedReviewsByProductId(@Param("productId") Long productId);

    // Batch query: lấy avgRating + count cho NHIỀU sản phẩm trong 1 query
    // Thay thế vòng lặp N×2 queries trong ProductFacade.mapToPagedResponse()
    // Trả về Object[]: [productId, avgRating, reviewCount]
    @Query("SELECT r.product.id, AVG(r.rating), COUNT(r) FROM Review r " +
           "WHERE r.product.id IN :productIds AND r.status = 'APPROVED' " +
           "GROUP BY r.product.id")
    List<Object[]> getAvgRatingAndCountByProductIds(@Param("productIds") Collection<Long> productIds);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.status = 'PENDING'")
    long countPendingReviews();
}
