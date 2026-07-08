package com.laptopstore.business.service;

import com.laptopstore.common.enums.ReviewStatus;
import com.laptopstore.data.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    
    Review createReview(Long userId, Long productId, Integer rating, String comment);
    
    Review updateReviewStatus(Long reviewId, ReviewStatus status);
    
    void deleteReview(Long reviewId);
    
    Page<Review> getApprovedReviewsByProduct(Long productId, Pageable pageable);
    
    Page<Review> getReviewsByUser(Long userId, Pageable pageable);
    
    Page<Review> getPendingReviews(Pageable pageable);
    
    Double getAverageRating(Long productId);
    
    boolean hasUserReviewedProduct(Long userId, Long productId);
    
    long countPendingReviews();
}
