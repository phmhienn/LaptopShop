package com.laptopstore.application.dto.product;

import lombok.Data;

@Data
public class ProductImageDTO {
    private Long id;
    private String imageUrl;
    private Integer displayOrder;
}
