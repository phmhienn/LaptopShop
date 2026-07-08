package com.laptopstore.application.dto.product;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long productId;
    private Integer rating;
    private String comment;
    private String status;
    private LocalDateTime createdAt;
}
