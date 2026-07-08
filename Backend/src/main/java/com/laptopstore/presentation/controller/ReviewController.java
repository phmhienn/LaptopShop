package com.laptopstore.presentation.controller;

import com.laptopstore.application.dto.product.ReviewCreateDTO;
import com.laptopstore.application.dto.product.ReviewDTO;
import com.laptopstore.application.facade.ReviewFacade;
import com.laptopstore.common.constants.AppConstants;
import com.laptopstore.common.response.ApiResponse;
import com.laptopstore.common.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewFacade reviewFacade;

    // Public Endpoint
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<ReviewDTO> response = reviewFacade.getApprovedReviewsByProduct(productId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // User Endpoint
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(@Valid @RequestBody ReviewCreateDTO requestDTO) {
        ReviewDTO createdReview = reviewFacade.createReview(requestDTO);
        return ResponseEntity.ok(ApiResponse.success("Review submitted and waiting for approval", createdReview));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getMyReviews(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<ReviewDTO> response = reviewFacade.getMyReviews(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Admin Endpoints
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewDTO>>> getPendingReviews(
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {
        
        PagedResponse<ReviewDTO> response = reviewFacade.getPendingReviews(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDTO>> approveReview(@PathVariable Long id) {
        ReviewDTO approvedReview = reviewFacade.approveReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review approved", approvedReview));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReviewDTO>> rejectReview(@PathVariable Long id) {
        ReviewDTO rejectedReview = reviewFacade.rejectReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review rejected", rejectedReview));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewFacade.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted successfully"));
    }
}
