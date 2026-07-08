package com.laptopstore.business.service.impl;

import com.laptopstore.business.exception.ResourceNotFoundException;
import com.laptopstore.business.exception.ValidationException;
import com.laptopstore.business.service.ProductService;
import com.laptopstore.business.service.ReviewService;
import com.laptopstore.business.service.UserService;
import com.laptopstore.common.enums.ReviewStatus;
import com.laptopstore.data.entity.Product;
import com.laptopstore.data.entity.Review;
import com.laptopstore.data.entity.User;
import com.laptopstore.data.repository.OrderRepository;
import com.laptopstore.data.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Review createReview(Long userId, Long productId, Integer rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5");
        }

        if (reviewRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new ValidationException("You have already reviewed this product");
        }
        
        if (!orderRepository.hasUserPurchasedProduct(userId, productId)) {
            throw new ValidationException("You can only review products you have purchased and received.");
        }
        
        User user = userService.getUserById(userId);
        Product product = productService.getProductById(productId);
        
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setStatus(ReviewStatus.APPROVED); // Automatically approve reviews
        
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReviewStatus(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
                
        review.setStatus(status);
        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review", "id", reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getApprovedReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getReviewsByUser(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> getPendingReviews(Pageable pageable) {
        return reviewRepository.findByStatus(ReviewStatus.PENDING, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? avg : 0.0;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return reviewRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingReviews() {
        return reviewRepository.countPendingReviews();
    }
}
