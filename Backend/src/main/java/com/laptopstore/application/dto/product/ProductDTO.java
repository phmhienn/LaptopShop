package com.laptopstore.application.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String slug;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String thumbnail;
    private String brandName;
    private String categoryName;
    private String status;
    private Boolean featured;
    private Double averageRating;
    private Long totalReviews;
    private LocalDateTime createdAt;
}
