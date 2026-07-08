package com.laptopstore.application.dto.product;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductDetailDTO {
    private Long id;
    private String name;
    private String slug;
    private String description;
    
    // Specs
    private String cpu;
    private String ram;
    private String ssd;
    private String gpu;
    private String display;
    private String battery;
    private BigDecimal weight;
    private Integer warranty;
    
    // Pricing & Status
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stock;
    private String thumbnail;
    private String status;
    private Boolean featured;
    
    // Relations
    private BrandDTO brand;
    private CategoryDTO category;
    private List<ProductImageDTO> images;
    
    // Stats
    private Double averageRating;
    private Long totalReviews;
}
