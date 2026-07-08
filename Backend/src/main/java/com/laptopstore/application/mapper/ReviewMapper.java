package com.laptopstore.application.mapper;

import com.laptopstore.application.dto.product.ReviewDTO;
import com.laptopstore.data.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewDTO toReviewDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        
        if (review.getUser() != null) {
            dto.setUserId(review.getUser().getId());
            dto.setUserName(review.getUser().getFullName());
            dto.setUserAvatar(review.getUser().getAvatar());
        }
        
        if (review.getProduct() != null) {
            dto.setProductId(review.getProduct().getId());
        }
        
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setStatus(review.getStatus().name());
        dto.setCreatedAt(review.getCreatedAt());

        return dto;
    }
}
