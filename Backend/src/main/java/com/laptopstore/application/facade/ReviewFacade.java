package com.laptopstore.application.facade;

import com.laptopstore.application.dto.product.ReviewCreateDTO;
import com.laptopstore.application.dto.product.ReviewDTO;
import com.laptopstore.application.mapper.ReviewMapper;
import com.laptopstore.application.security.services.UserDetailsImpl;
import com.laptopstore.business.service.ReviewService;
import com.laptopstore.common.enums.ReviewStatus;
import com.laptopstore.common.response.PagedResponse;
import com.laptopstore.data.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewFacade {

    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }

    @Transactional
    public ReviewDTO createReview(ReviewCreateDTO requestDTO) {
        Long userId = getCurrentUserId();
        Review review = reviewService.createReview(userId, requestDTO.getProductId(), 
                requestDTO.getRating(), requestDTO.getComment());
        return reviewMapper.toReviewDTO(review);
    }

    public PagedResponse<ReviewDTO> getApprovedReviewsByProduct(Long productId, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        return mapToPagedResponse(reviewService.getApprovedReviewsByProduct(productId, pageable));
    }

    public PagedResponse<ReviewDTO> getMyReviews(int page, int size, String sortBy, String sortDir) {
        Long userId = getCurrentUserId();
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        return mapToPagedResponse(reviewService.getReviewsByUser(userId, pageable));
    }

    // Admin Methods

    public PagedResponse<ReviewDTO> getPendingReviews(int page, int size, String sortBy, String sortDir) {
        Pageable pageable = getPageable(page, size, sortBy, sortDir);
        return mapToPagedResponse(reviewService.getPendingReviews(pageable));
    }

    @Transactional
    public ReviewDTO approveReview(Long reviewId) {
        return reviewMapper.toReviewDTO(reviewService.updateReviewStatus(reviewId, ReviewStatus.APPROVED));
    }

    @Transactional
    public ReviewDTO rejectReview(Long reviewId) {
        return reviewMapper.toReviewDTO(reviewService.updateReviewStatus(reviewId, ReviewStatus.REJECTED));
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewService.deleteReview(reviewId);
    }

    // Helper methods
    
    private Pageable getPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        return PageRequest.of(page, size, sort);
    }

    private PagedResponse<ReviewDTO> mapToPagedResponse(Page<Review> reviewsPage) {
        List<ReviewDTO> content = reviewsPage.getContent().stream()
                .map(reviewMapper::toReviewDTO)
                .collect(Collectors.toList());

        return PagedResponse.of(content, reviewsPage.getNumber(), reviewsPage.getSize(),
                reviewsPage.getTotalElements(), reviewsPage.getTotalPages());
    }
}
